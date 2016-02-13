package service;

import cache.TreeCache;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.stereotype.Service;
import pojo.Node;
import pojo.Path;
import utils.PatternMatchUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @Auther wanglp
 * @Time 15/11/21 下午3:36
 * @Email wanglp840@nenu.edu.cn
 */

@Service("treeService")
@Slf4j
public class TreeService {

    //设置搜索树数据
    TreeCache treeCache = TreeCache.builder().build();
    public void setDataUsed(TreeCache treeCache) {
        this.treeCache = treeCache;
    }


    // －－－－－－－－－－－－－－－查询－－－－－－－－－－－－－－－－－
    /**
     * 查询匹配结果排序列表
     */
    public List<String> prefixWordTopList(String queryWord) {
        List<Integer> allMatchRuleList = Lists.newArrayList();

        // 将词语切词并转换为code码
        List<Integer> queryCodeList = this.toCharacterCodeList(queryWord);

        // 前缀匹配词
        List<Integer> prefixRuleIdList = Lists.newArrayList();
        Node curNode = this.treeCache.getNodeList().get(0).get(0);
        this.allPrefixWordList(queryCodeList, prefixRuleIdList, curNode, 0);
        allMatchRuleList.addAll(prefixRuleIdList);

        // 查询串是中文时才进行中缀匹配词
        char startQuery = queryWord.charAt(0);
        if (PatternMatchUtil.isChinese(startQuery)){
            List<Integer> middleRuleList = Lists.newArrayList();
            this.allMiddleWordList(queryCodeList, middleRuleList);
            allMatchRuleList.addAll(middleRuleList);
        }

        // 查询类型为所有 或者 长度为0
        if (allMatchRuleList.size() == 0) {
            return Lists.newArrayList();
        }

        // 去重
        List<Integer> noPeatList = Lists.newArrayList();
        for (Integer ruleId : allMatchRuleList) {
            if (Collections.frequency(noPeatList, ruleId) < 1){
                noPeatList.add(ruleId);
            }
        }

        // 排序
        Collections.sort(noPeatList, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return Double.compare(treeCache.getRuleList().get(o2).getWeight(), treeCache.getRuleList().get(o1).getWeight());
            }
        });


        // 转换排序结果返回
        return Lists.transform(noPeatList, new Function<Integer, String>() {
            public String apply(Integer input) {
                return treeCache.getRuleList().get(input).getExpression();
            }
        }).subList(0, 100000 > noPeatList.size() ? noPeatList.size() : 15);
    }


    /**
     * 查询出前缀匹配的所有词语
     */
    private void allPrefixWordList(List<Integer> queryCodeList, List<Integer> ruleIdList, Node curNode, int curLevelId) {
        // 找到query的尾节点
        for (int code : queryCodeList) {
            // 出现树中没有的字
            if (code < 0) {
                return;
            }

            int index = Collections.binarySearch(curNode.pathList, code);
            if (index < 0) {
                return;
            } else {
                try{
                    curNode = treeCache.getNodeList().get(curLevelId + 1).get(curNode.pathList.get(index).toNodeId);
                }catch (Exception e){
                    log.error("treeCache.getNodeList出现异常", e);
                }
            }

            curLevelId++;
        }

        ruleIdList.clear();
        ruleIdList.addAll(curNode.ruleIdList);
    }


    /**
     * 中缀匹配查询
     */
    private void allMiddleWordList(List<Integer> queryCodeList, List<Integer> ruleList){
        int characterCode = queryCodeList.get(0);int levelCount = this.treeCache.getNodeList().size();
        List<Integer> tmpRuleList = Lists.newArrayList();

        // 逐层寻找该characterCode的Node
        for (int i = 1; i < levelCount; i++){
            List<Node> oneLevel = this.treeCache.getNodeList().get(i);
            for (int j = 0; j < oneLevel.size(); j++){
                int index = Collections.binarySearch(oneLevel.get(j).pathList, characterCode);
                if (index < 0){
                    continue;
                }

                tmpRuleList.clear();
                int nodeId = this.treeCache.getNodeList().get(i).get(j).pathList.get(index).toNodeId;
                Node curNode = this.treeCache.getNodeList().get(i+1).get(nodeId);
                if(queryCodeList.size() - 1 > 0){
                    this.allPrefixWordList(queryCodeList.subList(1, queryCodeList.size()), tmpRuleList, curNode, i+1);
                    ruleList.addAll(tmpRuleList);
                }else {
                    ruleList.addAll(curNode.ruleIdList);
                }
            }
        }
    }



    // －－－－－－－－－－－－－－－建树－－－－－－－－－－－－－－－－－

    /**
     * 插入词语
     */
    public void insertWordToTree(List<List<Node>> allNodeList, String word, String pinyin, String simplePinYin, Integer ruleCode, Map<Character, Integer> characterCodeMap) {
        // 插入汉字
        insertContentToTree(allNodeList, word, ruleCode, characterCodeMap);
        // 插入拼音
        insertContentToTree(allNodeList, pinyin, ruleCode, characterCodeMap);
        // 插入简拼
        insertContentToTree(allNodeList, simplePinYin, ruleCode, characterCodeMap);
    }

    private void insertContentToTree(List<List<Node>> allNodeList, String content, Integer ruleCode, Map<Character, Integer> characterCodeMap) {
        Node curNode = allNodeList.get(0).get(0);
        int levelId = 1;

        // 去除空格
        content = content.replace(" ","");
        // 一个一个节点插入

        char[] characterArr = content.toCharArray();
        for (int i = 0; i < characterArr.length; i++) {
            char curCharacter = characterArr[i];
            int pathCharacterCode = characterCodeMap.get(curCharacter);

            // 查看其是否已经存在
            int index = Collections.binarySearch(curNode.pathList, pathCharacterCode);
            if (index < 0) { // 不存在，则插入新节点,更新当前节点指向
                // 节点Id获取 每个level从0开始
                int nodeId = 0;
                if (allNodeList.size() > levelId){
                    nodeId = allNodeList.get(levelId).size();
                }

                Node tmpInsertNode = new Node(nodeId);
                Path tmpPath = new Path(pathCharacterCode, nodeId);
                tmpInsertNode.tmpValue = Character.toLowerCase(curCharacter);
                nodeId++;

                // 结束字
                if (i == characterArr.length - 1) {
                    tmpInsertNode.ruleIdList.add(ruleCode);
                }

                if (allNodeList.size() < levelId + 1){
                    List<Node> tmpList = Lists.newArrayList();
                    tmpList.add(tmpInsertNode);
                    allNodeList.add(levelId, tmpList);
                }else {
                    allNodeList.get(levelId).add(tmpInsertNode);
                }

                curNode.pathList.add(Math.abs(index + 1), tmpPath);
                curNode = tmpInsertNode;
            } else { // 存在 更新当前节点
                // 下一节点的nodeId
                int nextNodeId = curNode.pathList.get(index).toNodeId;
                curNode = allNodeList.get(levelId).get(nextNodeId);

                // 结束字
                if (i == characterArr.length - 1) {
                    curNode.ruleIdList.add(ruleCode);
                }
            }
            levelId++;
        }
    }


    /**
     * 设置所有节点的匹配串的ruleIdList
     */
    public void setNodeRuleList(Node curNode, int curLevelId) {
        if (curNode.pathList.size() == 0) {
            return;
        }

        List<Integer> ruleList = curNode.ruleIdList;
        for (Path path : curNode.pathList) {
            int nextNodeId = path.toNodeId;
            Node nextNode = treeCache.getNodeList().get(curLevelId + 1).get(nextNodeId);
            setNodeRuleList(nextNode, curLevelId + 1);

            ruleList.addAll(nextNode.ruleIdList);
        }
        curNode.ruleIdList = ruleList;
    }




    // －－－－－－－－－－－－－－－其他－－－－－－－－－－－－－－

    /**
     * 将汉字转换为code
     */
    private List<Integer> toCharacterCodeList(String word) {
        List<Integer> codeList = Lists.newArrayList();
        if (word.length() != 0) {
            char[] arr = word.toCharArray();
            // 不存在的转换为－3
            for (char c : arr) {
                Integer value = treeCache.getCharacterCodeMap().get(c);
                codeList.add(value == null ? -3 : value);
            }
        }
        return codeList;
    }

    /**
     * 处理查询字符串-中文开始则截取中文串搜索，字母开始搜索则将查询串中中文转换为全拼再查询
     */
    public String getTheHandledQuery(String oriQuery){
        StringBuilder sb = new StringBuilder();

        // 如果全是英文 全是中文，不处理
        if(PatternMatchUtil.isAllLetters(oriQuery) || PatternMatchUtil.isAllChinese(oriQuery)){
            return oriQuery;
        }

        char[] queryArr = oriQuery.toCharArray();
        // 首字母是中文 截取为首的中文串  首字母是拼音,将后面的汉字转换为拼音
        if(PatternMatchUtil.isChinese(queryArr[0])){
            for (char tmp : queryArr){
                if (PatternMatchUtil.isChinese(tmp)){
                    sb.append(tmp);
                }else {
                    break;
                }
            }
            return sb.toString();
        }else if (PatternMatchUtil.isLetter(queryArr[0])){
            // 转换工具
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

            for (char tmp : queryArr){
                if (PatternMatchUtil.isChinese(tmp)){
                    String pinyin[] = null;
                    try {
                        pinyin = PinyinHelper.toHanyuPinyinStringArray(tmp, format);
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        log.error("转换搜索关键字为拼音失败", e.getMessage());
                    }

                    if (pinyin != null) {
                        sb.append(pinyin[0]);
                    }
                }else {
                    sb.append(tmp);
                }
            }
            return sb.toString();
        }else { // 其他如数字 暂不处理
            return oriQuery;
        }
    }

    /**
     * 对查询在串中的位置计算（适用于拼音串中没有空格的情况）
     */
    public int[] getBoldPosition(String result, String queryWord){
        int[] position = new int[2];
        String[] oneLine = result.split("，");

        // 中文
        if (PatternMatchUtil.isChinese(queryWord.charAt(0))){
            position[0] = oneLine[0].indexOf(queryWord.charAt(0));
            position[1] = oneLine[0].indexOf(queryWord.charAt(queryWord.length()-1));
        }else {
            // 尝试匹配全拼
            if(oneLine[1].contains(queryWord)){
                // 中文占位+"," 因为是字母所有都是前缀匹配
                position[0] = oneLine[0].length()+1;
                position[1] = position[0] + queryWord.length() - 1;
            }else { // 简拼匹配
                position[0] = oneLine[0].length()+1 + oneLine[1].length() +1;
                position[1] = oneLine[0].length()+1 + oneLine[1].length() +1 + queryWord.length()-1;
            }
        }
        return position;
    }



}

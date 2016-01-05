package service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import common.entity.Node;
import common.entity.Path;
import common.other.TreeUsedData;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @Auther wanglp
 * @Time 15/11/21 下午3:36
 * @Email wanglp840@nenu.edu.cn
 */

@Service("treeService")
public class TreeService {
    private static Logger logger = Logger.getLogger("treeService name");
    TreeUsedData treeUsedData = new TreeUsedData();


    /**
     * 设置搜索树数据
     *
     * @param treeUsedData
     */
    public void setDataUsed(TreeUsedData treeUsedData) {
        this.treeUsedData = treeUsedData;
    }



    // －－－－－－－－－－－－－－－查询－－－－－－－－－－－－－－－－－
    /**
     * 查询前缀匹配排序结果列表
     *
     * @param queryWord
     * @param
     * @return
     */
    public List<String> prefixWordTopList(String queryWord) {
        List<Integer> allMatchRuleList = Lists.newArrayList();

        // 将词语切词并转换为code码
        List<Integer> queryCodeList = this.toCharacterCodeList(queryWord);

        // 前缀匹配词
        List<Integer> prefixRuleIdList = Lists.newArrayList();
        Node curNode = this.treeUsedData.getNodeList().get(0).get(0);
        this.allPrefixWordList(queryCodeList, prefixRuleIdList, curNode, 0);
        allMatchRuleList.addAll(prefixRuleIdList);
        // 中缀匹配词
        List<Integer> middleRuleList = Lists.newArrayList();
        this.allMiddleWordList(queryCodeList, middleRuleList);
        allMatchRuleList.addAll(middleRuleList);

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
                return Double.compare(treeUsedData.getRuleList().get(o2).getWeight(), treeUsedData.getRuleList().get(o1).getWeight());
            }
        });


        // 转换排序结果返回
        return Lists.transform(noPeatList, new Function<Integer, String>() {
            public String apply(Integer input) {
                return treeUsedData.getRuleList().get(input).getExpression();
            }
        }).subList(0, 15 > noPeatList.size() ? noPeatList.size() : 15);
    }

    /**
     * 查询出前缀匹配的所有词语
     *
     * @return
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
                    curNode = treeUsedData.getNodeList().get(curLevelId + 1).get(curNode.pathList.get(index).toNodeId);
                }catch (Exception e){
                    System.out.println(e);
                }
            }

            curLevelId++;
        }

        ruleIdList.clear();
        ruleIdList.addAll(curNode.ruleIdList);
    }


    private void allMiddleWordList(List<Integer> queryCodeList, List<Integer> ruleList){
        int characterCode = queryCodeList.get(0);int levelCount = this.treeUsedData.getNodeList().size();
        List<Integer> tmpRuleList = Lists.newArrayList();

        // 逐层寻找该characterCode的Node
        for (int i = 1; i < levelCount; i++){
            List<Node> oneLevel = this.treeUsedData.getNodeList().get(i);
            for (int j = 0; j < oneLevel.size(); j++){
                int index = Collections.binarySearch(oneLevel.get(j).pathList, characterCode);
                if (index < 0){
                    continue;
                }else {
                    tmpRuleList.clear();
                    int nodeId = this.treeUsedData.getNodeList().get(i).get(j).pathList.get(index).toNodeId;
                    Node curNode = this.treeUsedData.getNodeList().get(i+1).get(nodeId);

                    if(queryCodeList.size() - 1 > 0){
                        this.allPrefixWordList(queryCodeList.subList(1, queryCodeList.size()), tmpRuleList, curNode, i+1);
                        ruleList.addAll(tmpRuleList);
                    }else {
                        ruleList.addAll(curNode.ruleIdList);
                    }

                }
            }
        }
    }


    // －－－－－－－－－－－－－－－建树－－－－－－－－－－－－－－－－－


    /**
     * 插入词语
     *
     * @param allNodeList
     * @param word
     * @param pinyin
     * @param jianpin
     * @param characterCodeMap
     */
    public void insertWordToTree(List<List<Node>> allNodeList, String word, String pinyin, String jianpin, Integer ruleCode, Map<Character, Integer> characterCodeMap) {
        // 插入汉字
        insertContentToTree(allNodeList, word, word, ruleCode, characterCodeMap);
        // 插入拼音
        insertContentToTree(allNodeList, word, pinyin, ruleCode, characterCodeMap);
        // 插入简拼
        insertContentToTree(allNodeList, word, jianpin, ruleCode, characterCodeMap);

    }

    private void insertContentToTree(List<List<Node>> allNodeList, String word, String content, Integer ruleCode, Map<Character, Integer> characterCodeMap) {
        Node curNode = allNodeList.get(0).get(0);
        int levelId = 1;

        char[] characterArr = content.toCharArray();
        for (int i = 0; i < characterArr.length; i++) {
            int code = characterCodeMap.get(characterArr[i]);
            // 查看其是否已经存在
            int index = Collections.binarySearch(curNode.pathList, code);
            if (index < 0) { // 不存在，则插入新节点,更新当前节点指向
                int nodeId = 0;
                if (allNodeList.size() > levelId){
                    nodeId = allNodeList.get(levelId).size();
                }

                Node tmpInsertNode = new Node(nodeId);
                Path tmpPath = new Path(characterCodeMap.get(characterArr[i]), nodeId);
                tmpInsertNode.tmpValue = characterArr[i];
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


    public void setNodeRuleList(Node curNode, int curLevelId) {
        if (curNode.pathList.size() == 0) {
            return;
        }

        List<Integer> ruleList = curNode.ruleIdList;
        for (Path path : curNode.pathList) {
            int nextNodeId = path.toNodeId;
            Node nextNode = treeUsedData.getNodeList().get(curLevelId + 1).get(nextNodeId);
            setNodeRuleList(nextNode, curLevelId + 1);

            ruleList.addAll(nextNode.ruleIdList);
        }
        curNode.ruleIdList = ruleList;
    }


    // －－－－－－－－－－－－－－－其他－－－－－－－－－－－－－－

    /**
     * 将汉字转换为code
     *
     * @param word
     * @return
     */
    private List<Integer> toCharacterCodeList(String word) {
        List<Integer> codeList = Lists.newArrayList();
        if (word.length() != 0) {
            char[] arr = word.toCharArray();
            // 不存在的转换为－3
            for (char c : arr) {
                Integer value = treeUsedData.getCharacterCodeMap().get(c);
                codeList.add(value == null ? -3 : value);
            }
        }
        return codeList;
    }
}

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
    // 节点nodeId控制变量
    public static int nodeId = 1;


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
        // 将词语切词并转换为code码  获取结果
        List<Integer> queryCodeList = this.toCharacterCodeList(queryWord);
        List<Integer> ruleIdList = Lists.newArrayList();
        this.allPrefixWordList(queryCodeList, ruleIdList);

        // 查询类型为所有 或者 长度为0
        if (ruleIdList.size() == 0) {
            return Lists.newArrayList();
        }

        // 去重
        List<Integer> noPeatList = Lists.newArrayList();
        for (Integer ruleId : ruleIdList) {
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
    private void allPrefixWordList(List<Integer> queryCodeList, List<Integer> ruleIdList) {
        Node curNode = this.treeUsedData.getNodeList().get(0);

        // 找到query的尾节点
        int i = 0;
        for (int code : queryCodeList) {
            // 出现树中没有的字
            if (code < 0) {
                return;
            }

            int index = Collections.binarySearch(curNode.pathList, code);
            if (index < 0) {
                return;
            } else {
                curNode = treeUsedData.getNodeList().get(curNode.pathList.get(index).toNodeId);
            }
        }

        ruleIdList.clear();
        ruleIdList.addAll(curNode.ruleIdList);
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
    public void insertWordToTree(List<Node> allNodeList, String word, String pinyin, String jianpin, Integer ruleCode, Map<Character, Integer> characterCodeMap) {
        // 插入汉字
        insertContentToTree(allNodeList, word, word, ruleCode, characterCodeMap);
        // 插入拼音
        insertContentToTree(allNodeList, word, pinyin, ruleCode, characterCodeMap);
        // 插入简拼
        insertContentToTree(allNodeList, word, jianpin, ruleCode, characterCodeMap);

    }

    private void insertContentToTree(List<Node> allNodeList, String word, String content, Integer ruleCode, Map<Character, Integer> characterCodeMap) {
        Node curNode = allNodeList.get(0);

        char[] characterArr = content.toCharArray();
        for (int i = 0; i < characterArr.length; i++) {
            int code = characterCodeMap.get(characterArr[i]);
            // 查看其是否已经存在
            int index = Collections.binarySearch(curNode.pathList, code);
            if (index < 0) { // 不存在，则插入新节点,更新当前节点指向
                Node tmpInsertNode = new Node(nodeId);
                Path tmpPath = new Path(characterCodeMap.get(characterArr[i]), nodeId);
                tmpInsertNode.tmpValue = characterArr[i];

                nodeId++;
                // 结束字
                if (i == characterArr.length - 1) {
                    tmpInsertNode.ruleIdList.add(ruleCode);
                }

                allNodeList.add(tmpInsertNode);
                curNode.pathList.add(Math.abs(index + 1), tmpPath);
                curNode = tmpInsertNode;
            } else { // 存在 直接更新当前节点指向
                curNode = allNodeList.get(curNode.pathList.get(index).toNodeId);

                // 结束字
                if (i == characterArr.length - 1) {
                    curNode.ruleIdList.add(ruleCode);
                }
            }
        }
    }


    public void setNodeRuleList(Node curNode) {
        if (curNode.pathList.size() == 0) {
            return;
        }

        List<Integer> ruleList = curNode.ruleIdList;
        for (Path path : curNode.pathList) {
            Node next = treeUsedData.getNodeList().get(path.toNodeId);
            setNodeRuleList(next);
            ruleList.addAll(next.ruleIdList);
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

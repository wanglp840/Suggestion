package com.suggestion.cache;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.suggestion.pojo.Node;
import com.suggestion.pojo.Path;
import com.suggestion.pojo.Rule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 搜索树使用数据
 *
 * @Auther wanglp
 * @Time 15/11/23 上午12:11
 * @Email wanglp840@nenu.edu.cn
 */

@Getter
@Setter
@Builder
public class TreeCache {
    // 汉字(和字母)－code
    private Map<Character, Integer> characterCodeMap = Maps.newHashMap();

    // 树所有结点
    private List<List<Node>> nodeList = Lists.newArrayList();

    // 所有文件行
    private List<Rule> ruleList = Lists.newArrayList();

    private int chCode;
    private int ruleId;


    public void init() {

        characterCodeMap = Maps.newHashMap();
        nodeList = Lists.newArrayList();
        ruleList = Lists.newArrayList();
        // 26字母的code存储
        String abcStr = "abcdefgfhijklmnopqrstuvwxyz";
        for (char tmp : abcStr.toCharArray()) {
            characterCodeMap.put(tmp, chCode++);
        }


        Node rootNode = new Node(0);
        nodeList.add(Lists.newArrayList(rootNode));
    }


    /**
     * 计算所有节点匹配的所有RuleId
     */
    public void procNodeMatchRuleList() {
        procNodeMatchRuleList(nodeList.get(0).get(0), 0);
    }


    private void procNodeMatchRuleList(Node curNode, int curLevelId) {
        if (curNode.pathList.size() == 0) {
            return;
        }

        List<Integer> ruleList = curNode.matchRuleIdList;
        for (Path path : curNode.pathList) {
            int nextNodeId = path.toNodeId;
            Node nextNode = nodeList.get(curLevelId + 1).get(nextNodeId);
            procNodeMatchRuleList(nextNode, curLevelId + 1);
            ruleList.addAll(nextNode.matchRuleIdList);
        }
        curNode.matchRuleIdList = ruleList;
    }


    // －－－－－－－－－－－－－－－建树－－－－－－－－－－－－－－－－－

    /**
     * 插入词语
     */
    public int insertLineToTree(String[] line) {

        String word = line[0];
        String pinyin = line[1];
        String simplePinYin = line[2];

        // 字－code
        char[] charArr = word.toCharArray();
        for (char ch : charArr) {
            if (characterCodeMap.get(ch) == null) {
                characterCodeMap.put(ch, chCode++);
            }
        }

        // 插入汉字
        insertContentToTree(word, ruleId);
        // 插入拼音
        pinyin = pinyin.replace(" ","");
        insertContentToTree(pinyin, ruleId);
        // 插入简拼
        insertContentToTree(simplePinYin, ruleId);

        Rule rule = new Rule(ruleId, Joiner.on(",").join(line), Double.parseDouble(line[3]));
        ruleList.add(rule);
        return ruleId++;
    }

    private void insertContentToTree(String content, Integer ruleId) {
        Node curNode = nodeList.get(0).get(0);
        int levelId = 1;

        // 一个一个节点插入
        char[] characterArr = content.toCharArray();
        for (int i = 0; i < characterArr.length; i++) {
            char curCharacter = characterArr[i];
            int pathCharacterCode = characterCodeMap.get(curCharacter);

            // 查看其是否已经存在
            int index = Collections.binarySearch(curNode.pathList, pathCharacterCode);
            // 不存在插入新节点更新当前节点指向,存在直接当前节点
            if (index < 0) {
                // 节点Id获取 每个level从0开始
                int nodeId = 0;
                if (nodeList.size() > levelId) {
                    nodeId = nodeList.get(levelId).size();
                }

                // 插入节点构造 尾节点增加匹配ruleList
                Node tmpInsertNode = new Node(nodeId);
                Path tmpPath = new Path(pathCharacterCode, nodeId);
                tmpInsertNode.tmpValue = Character.toLowerCase(curCharacter);
                if (i == characterArr.length - 1) {
                    tmpInsertNode.matchRuleIdList.add(ruleId);
                }

                // 当前level还没有list新增并添加节点, 已经存在在该level的List直接插入节点
                if (nodeList.size() < levelId + 1) {
                    List<Node> tmpList = Lists.newArrayList();
                    tmpList.add(tmpInsertNode);
                    nodeList.add(levelId, tmpList);
                } else {
                    nodeList.get(levelId).add(tmpInsertNode);
                }

                curNode.pathList.add(Math.abs(index + 1), tmpPath);
                curNode = tmpInsertNode;
            } else {
                // 下一节点的nodeId
                int nextNodeId = curNode.pathList.get(index).toNodeId;
                curNode = nodeList.get(levelId).get(nextNodeId);

                // 结束字
                if (i == characterArr.length - 1) {
                    curNode.matchRuleIdList.add(ruleId);
                }
            }
            levelId++;
        }
    }
}

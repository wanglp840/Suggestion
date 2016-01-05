package common.other;

import common.entity.Node;
import common.entity.Rule;

import java.util.List;
import java.util.Map;

/**
 * 搜索树使用数据
 *
 * @Auther wanglp
 * @Time 15/11/23 上午12:11
 * @Email wanglp840@nenu.edu.cn
 */

public class TreeUsedData {
    // 字－code
    private Map<Character, Integer> characterCodeMap;

    // 所有结点
    private List<List<Node>> nodeList;

    // ruleList
    private List<Rule> ruleList;



    public List<List<Node>> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<List<Node>> nodeList) {
        this.nodeList = nodeList;
    }

    public Map<Character, Integer> getCharacterCodeMap() {
        return characterCodeMap;
    }

    public void setCharacterCodeMap(Map<Character, Integer> characterCodeMap) {
        this.characterCodeMap = characterCodeMap;
    }

    public List<Rule> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<Rule> ruleList) {
        this.ruleList = ruleList;
    }
}

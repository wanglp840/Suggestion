package common.other;

import common.entity.Node;

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
    // 根节点
    private Node rootNode;

    // 词
    private List<String> allWordList;
    // 词－权重
    private Map<String, Double> wordWeightMap;
    // 字－code
    private Map<Character, Integer> characterCodeMap;


    public Node getRootNode() {
        return rootNode;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public List<String> getAllWordList() {
        return allWordList;
    }

    public void setAllWordList(List<String> allWordList) {
        this.allWordList = allWordList;
    }

    public Map<String, Double> getWordWeightMap() {
        return wordWeightMap;
    }

    public void setWordWeightMap(Map<String, Double> wordWeightMap) {
        this.wordWeightMap = wordWeightMap;
    }

    public Map<Character, Integer> getCharacterCodeMap() {
        return characterCodeMap;
    }

    public void setCharacterCodeMap(Map<Character, Integer> characterCodeMap) {
        this.characterCodeMap = characterCodeMap;
    }
}

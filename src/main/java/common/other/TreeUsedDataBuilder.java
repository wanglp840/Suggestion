package common.other;

import common.entity.Node;

import java.util.List;
import java.util.Map;

/**
 * tree使用数据类，builder模式实现
 *
 * @Auther wanglp
 * @Time 15/12/13 上午12:11
 * @Email wanglp840@nenu.edu.cn
 */

public class TreeUsedDataBuilder {
    // 根节点
    private Node rootNode;

    // 词
    private List<String> allWordList;
    // 词－权重
    private Map<String, Double> wordWeightMap;
    // 字－code
    private Map<Character, Integer> characterCodeMap;

    public class Builder{
        private Node rootNode;
        private List<String> allWordList;
        private Map<String, Double> wordWeightMap;
        private Map<Character, Integer> characterCodeMap;

        public Builder(Node node, List<String> allWordList, Map<String, Double> wordWeightMap, Map<Character, Integer> characterCodeMap){
            this.rootNode = node;
            this.allWordList = allWordList;
            this.wordWeightMap = wordWeightMap;
            this.characterCodeMap = characterCodeMap;
        }

        public TreeUsedDataBuilder build(){
            return new TreeUsedDataBuilder(this);
        }
    }


    private TreeUsedDataBuilder(Builder builder){
        rootNode = builder.rootNode;
        allWordList = builder.allWordList;
        wordWeightMap = builder.wordWeightMap;
        characterCodeMap = builder.characterCodeMap;
    }
}

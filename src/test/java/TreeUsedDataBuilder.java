import com.google.common.collect.Lists;
import com.suggestion.pojo.Node;

import java.util.List;
import java.util.Map;

/**
 * @Auther wanglp
 * @Time 16/2/13 下午1:45
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


    private TreeUsedDataBuilder(Builder builder) {
        rootNode = builder.rootNode;
        allWordList = builder.allWordList;
        wordWeightMap = builder.wordWeightMap;
        characterCodeMap = builder.characterCodeMap;
    }

    public static Builder create() {
        return new Builder();
    }

    public static void main(String[] args) {
        TreeUsedDataBuilder treeUsedDataBuilder = TreeUsedDataBuilder.create().
                setAllWordList(Lists.newArrayList(" ")).
                setCharacterCodeMap(null).
                builder();
    }

    public static class Builder{
        private Node rootNode;
        private List<String> allWordList;
        private Map<String, Double> wordWeightMap;
        private Map<Character, Integer> characterCodeMap;

        public Builder(){

        }
        public Builder setRootNode(Node node){
            this.rootNode = node;
            return this;
        }
        public Builder setAllWordList(List<String> allWordList){
            this.allWordList = allWordList;
            return this;
        }
        public Builder setWordWeightMap(Map<String, Double> wordWeightMap){
            this.wordWeightMap = wordWeightMap;
            return this;
        }
        public Builder setCharacterCodeMap(Map<Character, Integer> characterCodeMap){
            this.characterCodeMap = characterCodeMap;
            return this;
        }

        public TreeUsedDataBuilder builder(){
            return new TreeUsedDataBuilder(this);
        }
    }
}

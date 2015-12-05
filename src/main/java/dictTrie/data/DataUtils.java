package dictTrie.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dictTrie.treeT.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

/**
 * @Auther wanglp
 * @Time 15/11/23 上午12:11
 * @Email wanglp840@nenu.edu.cn
 */

public class DataUtils {
    // 根节点
    private Node rootNode;

    // 词
    private List<String> allStrList;
    // 词－权重
    private Map<String, Double> weightMap;
    // 字－code
    private Map<Character, Integer> strCode;



    public Node getRootNode() {
        return rootNode;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public Map<String, Double> getWeightMap() {
        return weightMap;
    }

    public void setWeightMap(Map<String, Double> weightMap) {
        this.weightMap = weightMap;
    }

    public Map<Character, Integer> getStrCode() {
        return strCode;
    }

    public void setStrCode(Map<Character, Integer> strCode) {
        this.strCode = strCode;
    }

    public List<String> getAllStrList() {

        return allStrList;
    }

    public void setAllStrList(List<String> allStrList) {
        this.allStrList = allStrList;
    }
}

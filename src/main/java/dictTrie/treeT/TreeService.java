package dictTrie.treeT;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.javafx.sg.prism.NGShape;
import dictTrie.data.DataInput;
import dictTrie.data.DataUtils;
import dictTrie.enums.NodeType;
import dictTrie.enums.SearchType;
import dictTrie.other.SortPrefixWord;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * @Auther wanglp
 * @Time 15/11/21 下午3:36
 * @Email wanglp840@nenu.edu.cn
 */

@Service("treeService")
public class TreeService {

    DataUtils dataUtils = new DataUtils();
    Node rootNode;

    /**
     * 初始化数据
     * @param dataUtils
     */
    public void setDataUsed(DataUtils dataUtils){
        this.dataUtils = dataUtils;
        this.rootNode = dataUtils.getRootNode();
    }

    /**
     * 创建一棵树
     * @param
     */
    public Node buildDictTree(List<String> words, Node node, Map<String, Double> weightMap, Map<Character, Integer> strCode){
        for (String word : words){
            this.insert(word, node, weightMap, strCode);
        }
        return node;
    }


    /**
     * 插入词语
     * @param word
     */
    public void insert(String word, Node rootNode, Map<String, Double> weightMap, Map<Character, Integer> strCode) {
        Node curNode = rootNode;

        char[] wordArr = word.toCharArray();
        for (int i = 0; i < wordArr.length; i++) {

            // 获取该汉字的code值 生成新节点
            int code = strCode.get(wordArr[i]);
            Node tmpInsertNode = new Node(code, String.valueOf(wordArr[i]));

            // 结束字 设置叶节点标识 存储权重值
            if (i == wordArr.length - 1) {
                tmpInsertNode.nodeType = NodeType.LEAF;
                tmpInsertNode.weight = weightMap.get(word);
            }

            // 查看其是否已经存在
            int index = Collections.binarySearch(curNode.childNodeList, tmpInsertNode);
            if (index < 0) { // 不存在，则插入新节点,更新当前节点指向
                curNode.childNodeList.add(Math.abs(index + 1), tmpInsertNode);
                curNode = curNode.childNodeList.get(Math.abs(index + 1));
            } else { // 存在 直接更新当前节点指向
                curNode = curNode.childNodeList.get(index);
            }
        }

    }


    /**
     * 查看某个词语是否完全匹配
     *
     * @param str
     * @return
     */
    public boolean fullMatch(String str) {
        Node curNode = rootNode;

        // 转换为code码
        List<Integer> codeArr = this.getTheCodeArr(str);

        // 匹配词语
        int i = 0;
        for (Integer intTmp : codeArr) {
            Node tmpNode = new Node(intTmp, String.valueOf(str.charAt(i++)));
            int index = Collections.binarySearch(curNode.childNodeList, tmpNode);
            if (index < 0) {
                return false;
            } else {
                curNode = curNode.childNodeList.get(index);
            }
        }

        if (curNode.nodeType == NodeType.LEAF)
            return true;
        return false;
    }


    /**
     * 前序遍历输出树结构
     *
     * @return
     */
    public void preOrderDisplay(Node curNode) {

        if (curNode == null) {
            return;
        }

        System.out.println(curNode.code);
        if (curNode.nodeType == NodeType.BRanch) {
            for (Node tmp : curNode.childNodeList) {
                preOrderDisplay(tmp);
            }
        } else {
            System.out.println();
        }

    }

    /**
     * 查询出权重高的词语
     *
     * @param word
     * @param num
     * @return
     */
    public List<String> prefixWordsTop(String word, SearchType searchType, int num) {

        List<String> resultStr = Lists.newArrayList();
        List<SortPrefixWord> classList = Lists.newArrayList();

        // 获取结果
        this.prefixWords(word, resultStr, classList);

        // 查询类型为所有 或者 长度为0
        if (classList.size() == 0 || searchType == SearchType.SEARCH_ALL) {
            return resultStr;
        }

        // 对结果进行排序
        Collections.sort(classList, new Comparator<SortPrefixWord>() {
            public int compare(SortPrefixWord o1, SortPrefixWord o2) {
                return Double.compare(o2.weight, o1.weight);
            }
        });

        // 转换结果返回
        return Lists.transform(classList, new Function<SortPrefixWord, String>() {
            public String apply(SortPrefixWord sortPrefixWord) {
                return sortPrefixWord.word;
            }
        }).subList(0, num > classList.size() ? classList.size() : num);
    }

    /**
     * 查询出前缀匹配的所有单词
     *
     * @param word
     * @return
     */
    public void prefixWords(String word, List<String> strList, List<SortPrefixWord> classList) {

        /*List<String> queryWordList = Lists.newArrayList();*/
        Node curNode = this.rootNode;

        // 将词语切词并转换为code码
        List<Integer> codeList = this.getTheCodeArr(word);

        // 找到query的尾节点
        int i = 0;
        for (int code : codeList) {
            // 出现没有的字
            if (code < 0){
                return;
            }

            Node tmpNode = new Node(code, String.valueOf(word.charAt(i++)));
            int index = Collections.binarySearch(curNode.childNodeList, tmpNode);
            if (index < 0) {
                return;
            } else {
                curNode = curNode.childNodeList.get(index);
            }
        }

        this.searchWordByNode(curNode, word, strList, classList);
        /*return queryWordList;*/
    }

    /**
     * 根据当前节点找出其子节点
     *
     * @param node
     * @return
     */
    private void searchWordByNode(Node node, String str, List<String> queryWordList, List<SortPrefixWord> classList) {

        for (int i = 0; i < node.childNodeList.size(); i++) {
            //还有其他子节点
            Node childNode = node.childNodeList.get(i);
            if (childNode.childNodeList.size() != 0) {
                // 有叶子节点标识
                if (childNode.nodeType == NodeType.LEAF) {
                    queryWordList.add(str + childNode.value);
                }
                // 递归 递归str被改变 还原str
                str = str + childNode.value;
                searchWordByNode(childNode, str, queryWordList, classList);
                //
                str = str.substring(0, str.length() - 1);
            } else {
                queryWordList.add(str + childNode.value);

                SortPrefixWord sortPrefixWord = new SortPrefixWord(str + childNode.value, childNode.weight);
                classList.add(sortPrefixWord);
            }
        }
    }

    /**
     * 将汉字转换为code
     *
     * @param word
     * @return
     */
    private List<Integer> getTheCodeArr(String word) {
        List<Integer> codeList = Lists.newArrayList();
        if (word.length() != 0) {
            char[] arr = word.toCharArray();

            for (char c : arr) {
                Integer value = dataUtils.getStrCode().get(c);
                codeList.add(value == null ? -3 : value);
            }
        }
        return codeList;
    }
}

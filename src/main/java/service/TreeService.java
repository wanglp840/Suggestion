package service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.entity.Node;
import common.entity.SortPrefixWord;
import common.enums.TreeNodeType;
import common.other.TreeUsedData;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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


    /**
     * 查询前缀匹配排序结果列表
     *
     * @param queryWord
     * @param
     * @return
     */
    public List<String> prefixWordTopList(String queryWord) {

        List<String> allResultWordList = Lists.newArrayList();
        List<SortPrefixWord> resultSortClassList = Lists.newArrayList();

        // 将词语切词并转换为code码
        List<Integer> queryCodeList = this.toCharacterCodeList(queryWord);

        // 获取结果
        this.allPrefixWordList(queryWord, queryCodeList, allResultWordList, resultSortClassList);

        // 查询类型为所有 或者 长度为0
        if (resultSortClassList.size() == 0/* || searchType == SearchType.SEARCH_ALL*/) {
            return allResultWordList;
        }

        // 对简写和全拼出现的词语去重
        List<SortPrefixWord> noRepeat = Lists.newArrayList();
        for (SortPrefixWord sortPrefixWord : resultSortClassList){
            if (Collections.frequency(noRepeat, sortPrefixWord) < 1){
                noRepeat.add(sortPrefixWord);
            }
        }


        // 对结果进行排序
        Collections.sort(noRepeat, new Comparator<SortPrefixWord>() {
            public int compare(SortPrefixWord o1, SortPrefixWord o2) {
                return Double.compare(o2.weight, o1.weight);
            }
        });

        // 转换排序结果返回
        return Lists.transform(noRepeat, new Function<SortPrefixWord, String>() {
            public String apply(SortPrefixWord sortPrefixWord) {
                return sortPrefixWord.word;
            }
        }).subList(0, 10 > resultSortClassList.size() ? resultSortClassList.size() : 10);
    }

    /**
     * 查询出前缀匹配的所有词语
     *
     * @param word
     * @return
     */
    private void allPrefixWordList(String word, List<Integer> queryCodeList, List<String> resultList, List<SortPrefixWord> resultClassList) {
        Node curNode = this.treeUsedData.getRootNode();

        // 找到query的尾节点
        int i = 0;
        for (int code : queryCodeList) {
            // 出现树中没有的字
            if (code < 0) {
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

        // 当前节点是叶子节点 增加到结果中
        if (curNode.treeNodeType == TreeNodeType.LEAF) {
            // 字母  只添加其节点中的中文map
            if (curNode.wordWeightMap.size() != 0) {
                for (Map.Entry<String, Double> entry : curNode.wordWeightMap.entrySet()) {
                    resultClassList.add(new SortPrefixWord(entry.getKey(), entry.getValue()));
                }
            } else {
                resultList.add(word);
                SortPrefixWord sortPrefixWord = new SortPrefixWord(word, curNode.weight);
                resultClassList.add(sortPrefixWord);
            }
        }

        // 查询前缀匹配词语
        this.searchWordListByNode(curNode, word, resultList, resultClassList);
    }

    /**
     * 根据当前节点找出其子节点
     *
     * @param node
     * @return
     */
    private void searchWordListByNode(Node node, String preContent, List<String> resultList, List<SortPrefixWord> resultClassList) {
        for (int i = 0; i < node.childNodeList.size(); i++) {
            Node childNode = node.childNodeList.get(i);

            // 子节点还有子节点
            if (childNode.childNodeList.size() != 0) {
                // 有叶子节点标识 增加到结果
                if (childNode.treeNodeType == TreeNodeType.LEAF) {
                    // 字母 添加节点中的中文map
                    if (childNode.wordWeightMap.size() != 0) {
                        for (Map.Entry<String, Double> entry : childNode.wordWeightMap.entrySet()) {
                            resultClassList.add(new SortPrefixWord(entry.getKey(), entry.getValue()));
                        }
                    } else {
                        resultList.add(preContent + childNode.value);
                        SortPrefixWord sortPrefixWord = new SortPrefixWord(preContent + childNode.value, childNode.weight);
                        resultClassList.add(sortPrefixWord);
                    }
                }

                // 递归 递归str被改变 还原str
                preContent = preContent + childNode.value;
                searchWordListByNode(childNode, preContent, resultList, resultClassList);
                preContent = preContent.substring(0, preContent.length() - 1);
            } else {  // 结束节点 增加到结果

                // 字母结束节点
                if (childNode.wordWeightMap.size() != 0) {
                    for (Map.Entry<String, Double> entry : childNode.wordWeightMap.entrySet()) {
                        resultClassList.add(new SortPrefixWord(entry.getKey(), entry.getValue()));
                    }
                }else {
                    resultList.add(preContent + childNode.value);
                    SortPrefixWord sortPrefixWord = new SortPrefixWord(preContent + childNode.value, childNode.weight);
                    resultClassList.add(sortPrefixWord);
                }
            }
        }
    }

    // －－－－－－－－－－－－－－－建树－－－－－－－－－－－－－－－－－


    /**
     * 插入词语
     *
     * @param rootNode
     * @param word
     * @param pinyin
     * @param jianpin
     * @param weight
     * @param characterCodeMap
     */
    public void insertWordToTree(Node rootNode, String word, String pinyin, String jianpin, Double weight, Map<Character, Integer> characterCodeMap) {
        // 插入汉字
        insertContentToTree(rootNode, word, word, weight, characterCodeMap);
        // 插入拼音
        insertContentToTree(rootNode, word, pinyin, weight, characterCodeMap);
        // 插入简拼
        insertContentToTree(rootNode, word, jianpin, weight, characterCodeMap);

    }

    private void insertContentToTree(Node rootNode, String word, String content, Double weight, Map<Character, Integer> characterCodeMap) {
        Node curNode = rootNode;

        // 检测字母
        String regex = ".*[a-zA-Z]+.*";
        boolean hasLetter = Pattern.compile(regex).matcher(content).matches();

        char[] characterArr = content.toCharArray();
        for (int i = 0; i < characterArr.length; i++) {
            // 获取该汉字的code值 生成新节点
            int code = characterCodeMap.get(characterArr[i]);
            Node tmpInsertNode = new Node(code, String.valueOf(characterArr[i]));

            // 结束字 设置叶节点标识 存储权重值
            if (i == characterArr.length - 1) {
                tmpInsertNode.treeNodeType = TreeNodeType.LEAF;
                tmpInsertNode.weight = weight;

                // 字母建树 存储word－权重
                if (hasLetter) {
                    tmpInsertNode.wordWeightMap.put(word, weight);
                }
            }

            // 查看其是否已经存在
            int index = Collections.binarySearch(curNode.childNodeList, tmpInsertNode);
            if (index < 0) { // 不存在，则插入新节点,更新当前节点指向
                curNode.childNodeList.add(Math.abs(index + 1), tmpInsertNode);
                curNode = curNode.childNodeList.get(Math.abs(index + 1));
            } else { // 存在 直接更新当前节点指向
                curNode = curNode.childNodeList.get(index);

                // 字母节点  拼音或者简拼相同但是词语是不同的
                if (i == characterArr.length - 1 && hasLetter) {
                    curNode.wordWeightMap.put(word, weight);
                }
            }
        }
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


    /**
     * /**
     * 前序遍历输出树结构
     *
     * @return
     */
    public void preOrderDisplayTreeNode(Node curNode) {
        if (curNode == null) {
            return;
        }

        // 输出节点内容
        System.out.println(curNode.code + " " + curNode.value);

        // 递归
        if (curNode.treeNodeType == TreeNodeType.BRanch) {
            for (Node tmpNode : curNode.childNodeList) {
                preOrderDisplayTreeNode(tmpNode);
            }
        } else {
            System.out.println("－－－－");
        }
    }

    /**
     * 查看某个词语是否完全匹配-暂只支持汉字
     *
     * @param word
     * @return
     */
    public boolean isWordFullMath(String word) {
        Node curNode = treeUsedData.getRootNode();

        // 转换为code码
        List<Integer> characterCodeArr = this.toCharacterCodeList(word);

        // 匹配词语
        int i = 0;
        for (Integer code : characterCodeArr) {
            Node tmpNode = new Node(code, String.valueOf(word.charAt(i++)));
            int index = Collections.binarySearch(curNode.childNodeList, tmpNode);
            if (index < 0) {
                return false;
            } else {
                curNode = curNode.childNodeList.get(index);
            }
        }

        // 匹配到叶子节点
        if (curNode.treeNodeType == TreeNodeType.LEAF)
            return true;
        return false;
    }
}

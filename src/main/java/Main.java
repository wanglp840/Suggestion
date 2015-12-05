import dictTrie.data.DataInput;
import dictTrie.data.DataUtils;
import dictTrie.enums.SearchType;
import dictTrie.treeT.Node;
import dictTrie.treeT.TreeService;

import java.util.List;
import java.util.Set;


/**
 * @Auther wanglp
 * @Time 15/9/4 下午10:08
 * @Email wanglp840@nenu.edu.cn
 */

public class Main {


    public static void main(String[] args) throws Exception{

        TreeService treeService = new TreeService();
        final DataUtils dataUtils = new DataUtils();

        // 数据获取
        DataInput.getTheData();

        // 创建一颗树
        Node rootNode = new Node(-1, " ");
        rootNode = treeService.buildDictTree(DataInput.allStrList, rootNode, DataInput.weightMap, DataInput.strCode);

        // 设置当前树使用的数据
        dataUtils.setAllStrList(DataInput.allStrList);
        dataUtils.setWeightMap(DataInput.weightMap);
        dataUtils.setStrCode(DataInput.strCode);
        dataUtils.setRootNode(rootNode);

        treeService.setDataUsed(dataUtils);




        // 测试词语完全匹配
        System.out.println(treeService.fullMatch("打豆豆"));

        List<String> tmpList;
        System.out.println("--------打豆豆的前缀匹配");
        tmpList = treeService.prefixWordsTop("打豆豆", SearchType.SEARCH_TOP, 5);
        if (tmpList.size() == 0){
            System.out.println("没有可以匹配的啊啊");
        }else {
            System.out.println(tmpList);
        }

        System.out.println("--------我是好处 的前缀匹配");
        tmpList = treeService.prefixWordsTop("我是好处", SearchType.SEARCH_TOP, 5);
        if (tmpList.size() == 0){
            System.out.println("没有可以匹配的啊啊");
        }else {
            System.out.println(tmpList);
        }

        System.out.println("--------我是好词 的前缀匹配");
        tmpList = treeService.prefixWordsTop("我是好词", SearchType.SEARCH_TOP, 5);
        if (tmpList.size() == 0){
            System.out.println("没有可以匹配的啊啊");
        }else {
            System.out.println(tmpList);
        }



        // 前序输出树结构
        /*tree.preOrderDisplay(tree.getRoot());*/

        /*//测试前缀匹配输出
        Set<String> tmpMap;

        System.out.println("--------我");
        tmpMap = tree.prefixWords("我");
        if (tmpMap.size() == 0){
            System.out.println("pipei 0 ge");
        }else {
            for (String s : tmpMap) {
                System.out.println(s);
            }
        }

        System.out.println("--------词");
        tmpMap = tree.prefixWords("词");
        if (tmpMap.size() == 0){
            System.out.println("pipei 0 ge");
        }else {
            for (String s : tmpMap) {
                System.out.println(s + " " + DataConstants.weightMap.get(s));
            }

            System.out.println("--------词  top3排序");
            List<String> topList = tree.prefixWordsTop("词", 3);
            for (String s : topList){
                System.out.println(s + " " + DataConstants.weightMap.get(s));
            }
        }
        */


       /* System.out.println("--------词  top3排序");
        List<String> topList = tree.prefixWordsTop("词", SearchType.SEARCH_ALL, 3);
        for (String s : topList){
            System.out.println(s + " " + DataConstants.weightMap.get(s));
        }*/




    }
}

import common.data.DataUtils;
import common.enums.SearchType;
import common.entity.Node;
import common.other.TreeUsedData;
import service.TreeService;

import java.util.List;


/**
 * @Auther wanglp
 * @Time 15/9/4 下午10:08
 * @Email wanglp840@nenu.edu.cn
 */

public class Main {


    public static void main(String[] args) throws Exception{

        TreeService treeService = new TreeService();
        final TreeUsedData treeUsedData = new TreeUsedData();

        // 数据获取
        DataUtils.getTheData();

        // 创建一颗树
        Node rootNode = new Node(-1, " ");
        rootNode = treeService.buildDictTree(DataUtils.allWordList, rootNode, DataUtils.wordWeightMap, DataUtils.characterCodeMap);

        // 设置当前树使用的数据
        treeUsedData.setAllWordList(DataUtils.allWordList);
        treeUsedData.setWordWeightMap(DataUtils.wordWeightMap);
        treeUsedData.setCharacterCodeMap(DataUtils.characterCodeMap);
        treeUsedData.setRootNode(rootNode);

        treeService.setDataUsed(treeUsedData);




        // 测试词语完全匹配
        System.out.println(treeService.isWordFullMath("打豆豆"));

        List<String> tmpList;
        System.out.println("--------打豆豆的前缀匹配");
        tmpList = treeService.prefixWordTopList("打豆豆", SearchType.SEARCH_TOP, 5);
        if (tmpList.size() == 0){
            System.out.println("没有可以匹配的啊啊");
        }else {
            System.out.println(tmpList);
        }

        System.out.println("--------我是好处 的前缀匹配");
        tmpList = treeService.prefixWordTopList("我是好处", SearchType.SEARCH_TOP, 5);
        if (tmpList.size() == 0){
            System.out.println("没有可以匹配的啊啊");
        }else {
            System.out.println(tmpList);
        }

        System.out.println("--------我是好词 的前缀匹配");
        tmpList = treeService.prefixWordTopList("我是好词", SearchType.SEARCH_TOP, 5);
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

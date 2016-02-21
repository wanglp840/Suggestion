import com.suggestion.service.IndexService;

import java.util.List;


/**
 * @Auther wanglp
 * @Time 15/9/4 下午10:08
 * @Email wanglp840@nenu.edu.cn
 */

public class TestMain {


    public static void main(String[] args) throws Exception {

        IndexService indexService = new IndexService();


        // 测试词语完全匹配
//        System.out.println(indexService.isWordFullMath("打豆豆"));

        List<String> tmpList;
        System.out.println("--------打豆豆的前缀匹配");
        tmpList = indexService.search("打豆豆");
        if (tmpList.size() == 0) {
            System.out.println("没有可以匹配的啊啊");
        } else {
            System.out.println(tmpList);
        }

        System.out.println("--------我是好处 的前缀匹配");
        tmpList = indexService.search("我是好处");
        if (tmpList.size() == 0) {
            System.out.println("没有可以匹配的啊啊");
        } else {
            System.out.println(tmpList);
        }

        System.out.println("--------我是好词 的前缀匹配");
        tmpList = indexService.search("我是好词");
        if (tmpList.size() == 0) {
            System.out.println("没有可以匹配的啊啊");
        } else {
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

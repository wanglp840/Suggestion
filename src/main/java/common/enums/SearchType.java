package common.enums;

/**
 * 智能搜索的类型
 *
 * @Auther wanglp
 * @Time 15/11/26 下午1:18
 * @Email wanglp840@nenu.edu.cn
 */

public enum SearchType {
    SEARCH_ALL(0, "前缀匹配的所有词"),
    SEARCH_TOP(1, "前缀匹配的权重排序部分词")
    ;


    private Integer id;
    private String desc;
    SearchType(Integer id, String desc){
        this.id = id;
        this.desc = desc;
    }
}

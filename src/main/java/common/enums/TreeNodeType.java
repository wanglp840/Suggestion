package common.enums;

/**
 * 树节点的类型
 *
 * @Auther wanglp
 * @Time 15/11/21 下午3:31
 * @Email wanglp840@nenu.edu.cn
 */

public enum TreeNodeType {
    LEAF(0, "叶子节点"),
    BRanch(1, "主干节点")
    ;

    private Integer id;
    private String desc;


    TreeNodeType(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

}

package common.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.enums.TreeNodeType;

import java.util.List;
import java.util.Map;

/**
 * 搜索树节点类
 *
 * @Auther wanglp
 * @Time 15/11/22 下午11:31
 * @Email wanglp840@nenu.edu.cn
 */

public class Node implements Comparable<Node>{

    // 汉字编号
    public int code = 0;
    // 字
    public String value = null;
    // 子节点
    public List<Node> childNodeList = Lists.newArrayList();


    // 节点类别
    public TreeNodeType treeNodeType = null;
    // 节点的权重（叶子节点）
    public double weight = 0.0;


    // 字母搜索－叶子节点的中文list(存储词语－权重)
    public Map<String, Double> wordWeightMap = Maps.newHashMap();


    // 查询只需使用code，和根节点使用
    public Node(int code, String value){
        this.code = code;
        this.value = value;
    }


    public int compareTo(Node anotherNode){
        return code < anotherNode.code ? -1:code == anotherNode.code ? 0:1 ;
    }
}

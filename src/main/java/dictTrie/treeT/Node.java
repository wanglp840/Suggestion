package dictTrie.treeT;

import com.google.common.collect.Lists;
import dictTrie.enums.NodeType;

import java.util.List;

/**
 * @Auther wanglp
 * @Time 15/11/22 下午11:31
 * @Email wanglp840@nenu.edu.cn
 */

public class Node implements Comparable<Node>{

    public int code = 0;
    public String value = null;
    public List<Node> childNodeList = Lists.newArrayList();

    public NodeType nodeType = null;
    public double weight = 0.0;

    public Node(int code, String value){
        this.code = code;
        this.value = value;
    }


    public int compareTo(Node anotherNode){
        return code < anotherNode.code ? -1:code == anotherNode.code ? 0:1 ;
    }
}

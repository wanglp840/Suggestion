package common.entity;

import com.google.common.primitives.Ints;

/**
 * @Auther wanglp
 * @Time 16/1/1 上午11:40
 * @Email wanglp840@nenu.edu.cn
 */

public class Path implements Comparable<Integer>{

    // 转换条件－字code
    public int characterCode;
    // 转换到节点
    public int toNodeId;




    public Path(int characterCode, int toNodeId){
        this.characterCode = characterCode;
        this.toNodeId = toNodeId;
    }


    @Override
    public String toString() {
        return "Path{" +
                "characterCode=" + characterCode +
                ", toNodeId=" + toNodeId +
                '}';
    }

    public int compareTo(Integer o) {
        return Ints.compare(characterCode, o);
    }
}

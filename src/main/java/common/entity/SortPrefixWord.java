package common.entity;

/**
 * 用于权重排序结果 的词语类
 *
 * @Auther wanglp
 * @Time 15/11/25 下午11:38
 * @Email wanglp840@nenu.edu.cn
 */

public class SortPrefixWord {
    // 词语
    public String word;
    // 搜索词语的权重
    public Double weight;

    public SortPrefixWord(String word, Double weight) {
        this.word = word;
        this.weight = weight;
    }


    public boolean equals(Object o){
        if (this == o){
            return true;
        }
        if (o == null){
            return false;
        }
        if (getClass() != o.getClass()){
            return false;
        }

        SortPrefixWord sortPrefixWord = (SortPrefixWord)o;
        if (word.equals(sortPrefixWord.word)){
            return true;
        }else {
            return false;
        }
    }
}

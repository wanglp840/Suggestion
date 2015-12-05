package dictTrie.other;

import sun.nio.ch.ThreadPool;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Auther wanglp
 * @Time 15/11/25 下午11:38
 * @Email wanglp840@nenu.edu.cn
 */

public class SortPrefixWord {
    public String word;
    public Double weight;

    public SortPrefixWord(String word, Double weight) {
        this.word = word;
        this.weight = weight;
    }

}

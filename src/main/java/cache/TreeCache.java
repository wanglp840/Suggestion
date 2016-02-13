package cache;

import lombok.Getter;
import lombok.Setter;
import pojo.Node;
import pojo.Rule;
import java.util.List;
import java.util.Map;

/**
 * 搜索树使用数据
 *
 * @Auther wanglp
 * @Time 15/11/23 上午12:11
 * @Email wanglp840@nenu.edu.cn
 */

@Getter
@Setter
public class TreeCache {
    // 字－code
    private Map<Character, Integer> characterCodeMap;

    // 所有结点
    private List<List<Node>> nodeList;

    // ruleList
    private List<Rule> ruleList;

}

package com.suggestion.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.suggestion.cache.TreeCache;
import com.suggestion.pojo.Node;
import com.suggestion.utils.PatternMatchUtil;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Auther wanglp
 * @Time 15/11/21 下午3:36
 * @Email wanglp840@nenu.edu.cn
 */

@Service
@Log4j2
@Setter
public class IndexService {

    //设置搜索树数据
    private TreeCache treeCache = TreeCache.builder().build();

    // －－－－－－－－－－－－－－－查询－－－－－－－－－－－－－－－－－

    /**
     * 查询匹配结果排序列表
     */
    public List<String> search(String queryWord) {

        // 将词语切词并转换为code码
        List<Integer> queryCodeList = toCharacterCodeList(queryWord);

        // 前缀匹配词
        Node curNode = treeCache.getNodeList().get(0).get(0);
        List<Integer> matchedRule = prefixSearch(queryCodeList, curNode, 0);

        //如果结果不足30条并且是中文 转为中缀匹配
        char startQuery = queryWord.charAt(0);
        if (matchedRule.size() < 30 && PatternMatchUtil.isChinese(startQuery)) {
            matchedRule.addAll(middleSearch(queryCodeList));
        }

        return transformResult(matchedRule);
    }

    private List<String> transformResult(List<Integer> allMatchRuleList) {
        // 查询类型为所有 或者 长度为0
        if (allMatchRuleList.size() == 0) {
            return Lists.newArrayList();
        }

        // 去重
        List<Integer> noPeatList = Lists.newArrayList();
        for (Integer ruleId : allMatchRuleList) {
            if (Collections.frequency(noPeatList, ruleId) < 1) {
                noPeatList.add(ruleId);
            }
        }

        // 排序
        Collections.sort(noPeatList, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return Double.compare(treeCache.getRuleList().get(o2).getWeight(), treeCache.getRuleList().get(o1).getWeight());
            }
        });


        // 转换排序结果返回
        return Lists.transform(noPeatList, new Function<Integer, String>() {
            public String apply(Integer input) {
                return treeCache.getRuleList().get(input).getExpression();
            }
        }).subList(0, 100000 > noPeatList.size() ? noPeatList.size() : 15);
    }


    /**
     * 查询出前缀匹配的所有词语
     */
    private List<Integer> prefixSearch(List<Integer> queryCodeList, Node curNode, int curLevelId) {

        List<Integer> result = Lists.newArrayList();
        // 找到query的尾节点
        for (int code : queryCodeList) {
            // 出现树中没有的字
            if (code < 0) {
                return result;
            }

            int index = Collections.binarySearch(curNode.pathList, code);
            if (index < 0) {
                return result;
            } else {
                try {
                    curNode = treeCache.getNodeList().get(curLevelId + 1).get(curNode.pathList.get(index).toNodeId);
                } catch (Exception e) {
                    log.error("treeCache.getNodeList出现异常", e);
                }
            }

            curLevelId++;
        }

        result.addAll(curNode.matchRuleIdList);
        return result;
    }


    /**
     * 中缀匹配查询
     */
    private List<Integer> middleSearch(List<Integer> queryCodeList) {
        int characterCode = queryCodeList.get(0);
        int levelCount = treeCache.getNodeList().size();
        List<Integer> tmpRuleList = Lists.newArrayList();

        // 逐层寻找该characterCode的Node
        for (int i = 1; i < levelCount; i++) {
            List<Node> oneLevel = treeCache.getNodeList().get(i);
            for (int j = 0; j < oneLevel.size(); j++) {
                int index = Collections.binarySearch(oneLevel.get(j).pathList, characterCode);
                if (index < 0) {
                    continue;
                }

                int nodeId = treeCache.getNodeList().get(i).get(j).pathList.get(index).toNodeId;
                Node curNode = treeCache.getNodeList().get(i + 1).get(nodeId);
                if (queryCodeList.size() - 1 > 0) {
                    tmpRuleList = prefixSearch(queryCodeList.subList(1, queryCodeList.size()), curNode, i + 1);
                    if (CollectionUtils.isEmpty(tmpRuleList)) {
                        continue;
                    }
                    return tmpRuleList;
                } else {
                    tmpRuleList.addAll(curNode.matchRuleIdList);
                    return tmpRuleList;
                }
            }
        }

        return tmpRuleList;
    }


    // －－－－－－－－－－－－－－－其他－－－－－－－－－－－－－－

    /**
     * 将汉字转换为code
     */
    private List<Integer> toCharacterCodeList(String word) {
        List<Integer> codeList = Lists.newArrayList();
        if (word.length() != 0) {
            char[] arr = word.toCharArray();
            // 不存在的转换为－3
            for (char c : arr) {
                Integer value = treeCache.getCharacterCodeMap().get(c);
                codeList.add(value == null ? -3 : value);
            }
        }
        return codeList;
    }

    /**
     * 处理查询字符串-中文开始则截取中文串搜索，字母开始搜索则将查询串中中文转换为全拼再查询
     */
    public String getTheHandledQuery(String oriQuery) {
        StringBuilder sb = new StringBuilder();

        // 如果全是英文 全是中文，不处理
        if (PatternMatchUtil.isAllLetters(oriQuery) || PatternMatchUtil.isAllChinese(oriQuery)) {
            return oriQuery;
        }

        char[] queryArr = oriQuery.toCharArray();
        // 首字母是中文 截取为首的中文串  首字母是拼音,将后面的汉字转换为拼音
        if (PatternMatchUtil.isChinese(queryArr[0])) {
            for (char tmp : queryArr) {
                if (PatternMatchUtil.isChinese(tmp)) {
                    sb.append(tmp);
                } else {
                    break;
                }
            }
            return sb.toString();
        } else if (PatternMatchUtil.isLetter(queryArr[0])) {
            // 转换工具
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

            for (char tmp : queryArr) {
                if (PatternMatchUtil.isChinese(tmp)) {
                    String pinyin[] = null;
                    try {
                        pinyin = PinyinHelper.toHanyuPinyinStringArray(tmp, format);
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        log.error("转换搜索关键字为拼音失败", e);
                    }

                    if (pinyin != null) {
                        sb.append(pinyin[0]);
                    }
                } else {
                    sb.append(tmp);
                }
            }
            return sb.toString();
        } else { // 其他如数字 暂不处理
            return oriQuery;
        }
    }

    /**
     * 对查询在串中的位置计算（适用于拼音串中没有空格的情况）
     */
    public int[] getBoldPosition(String result, String queryWord) {
        int[] position = new int[2];
        String[] oneLine = result.split("，");

        // 中文
        if (PatternMatchUtil.isChinese(queryWord.charAt(0))) {
            position[0] = oneLine[0].indexOf(queryWord.charAt(0));
            position[1] = oneLine[0].indexOf(queryWord.charAt(queryWord.length() - 1));
        } else {
            // 尝试匹配全拼
            if (oneLine[1].contains(queryWord)) {
                // 中文占位+"," 因为是字母所有都是前缀匹配
                position[0] = oneLine[0].length() + 1;
                position[1] = position[0] + queryWord.length() - 1;
            } else { // 简拼匹配
                position[0] = oneLine[0].length() + 1 + oneLine[1].length() + 1;
                position[1] = oneLine[0].length() + 1 + oneLine[1].length() + 1 + queryWord.length() - 1;
            }
        }
        return position;
    }


}

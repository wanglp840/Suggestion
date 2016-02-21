package com.suggestion.utils;

import java.util.regex.Pattern;

/**
 * @Auther wanglp
 * @Time 16/1/7 下午6:35
 * @Email wanglp840@nenu.edu.cn
 */

public class PatternMatchUtil {
    static Pattern chineseP = Pattern.compile("[\u4e00-\u9fa5]");
    static Pattern letterP = Pattern.compile("[a-z]+");


    /**
     * 判断是否全是字母
     */
    public static boolean isAllLetters(String input){
        return letterP.matcher(input).matches();
    }

    /**
     * 判断是否全是汉字
     */
    public static boolean isAllChinese(String input){
        return chineseP.matcher(input).matches();
    }

    /**
     * 是否是字母
     */
    public static boolean isLetter(char input){
        return letterP.matcher(input + "").matches();
    }

    /**
     * 是否是汉字
     */
    public static boolean isChinese(char input){
        return chineseP.matcher(input + "").matches();
    }
}

package utils;

/**
 * @Auther wanglp
 * @Time 16/2/13 下午1:46
 * @Email wanglp840@nenu.edu.cn
 */

public final class Constants {
    // 首页
    public static final String INDEX = "";

    // 搜索
    public static final String INDEX_SEARCH = "public/search";





    // 数据的文件名
    public static final String dataSourceFileName;

    static {
        PropertiesLoader propertiesLoader = new PropertiesLoader("wlp_suggestion.properties");

        dataSourceFileName = (String)propertiesLoader.getProperty("dataSourceFileName");

    }

}

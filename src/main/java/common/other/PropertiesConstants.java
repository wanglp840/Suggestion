package common.other;

import common.utils.PropertiesLoader;

/**
 * @Auther wanglp
 * @Time 15/12/9 上午10:25
 * @Email wanglp840@nenu.edu.cn
 */

public class PropertiesConstants {

    // 数据的文件名
    public static final String dataSourceFileName;

    static {
        PropertiesLoader propertiesLoader = new PropertiesLoader("wlp_suggestion.properties");

        dataSourceFileName = (String)propertiesLoader.getProperty("dataSourceFileName");

    }
}

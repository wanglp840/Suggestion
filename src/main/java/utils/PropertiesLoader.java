package utils;

import org.apache.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Auther wanglp
 * @Time 15/12/9 上午11:15
 * @Email wanglp840@nenu.edu.cn
 */

public class PropertiesLoader {

    private static final Logger logger = Logger.getLogger(PropertiesLoader.class);

    // 存储properties文件中的键值对 内部hashTable实现
    private Properties properties;
    // 加载配置文件类
    private static ResourceLoader resourceLoader = new DefaultResourceLoader();

    public PropertiesLoader(String... resourcesPaths){
        properties = loadProperties(resourcesPaths);
    }

    // 加载所有的配置文件
    private Properties loadProperties(String... resourcePaths){
        Properties pro = new Properties();

        InputStream in = null;
        for (String path : resourcePaths){
            try {
                Resource resource = resourceLoader.getResource(path);
                in = resource.getInputStream();
                pro.load(in);
            }catch (IOException e){
                logger.error("Could not load properties from path:" + path + "," + e.getMessage());
            }
            finally {
                if (in != null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return pro;
    }




    // －－－－－－－－－根据key值获得value值

    // 取出Property，但以System的Property优先
    private String getValue(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            return systemProperty;
        }
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        return "";
    }

    // 为空时自定义默认值
    public Object getProperty(String key, Object defaultObject){
        Object value = this.getValue(key);
        return value == null ? defaultObject : value;
    }

    // 为空 返回空值 后改为返回“”
    public Object getProperty(String key){
        return this.getProperty(key, null);
    }
}

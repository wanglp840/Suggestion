package common.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.other.PropertiesConstants;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 数据处理类-获取处理数据
 *
 * @Auther wanglp
 * @Time 15/11/23 上午12:11
 * @Email wanglp840@nenu.edu.cn
 */

public class DataUtils {
    private static Logger logger = Logger.getLogger(DataUtils.class.getName());

    // 数据文件
    private static String  fileName = DataUtils.class.getClassLoader().getResource(PropertiesConstants.dataSourceFileName).getFile();

    // 词
    public static List<String> allWordList = Lists.newArrayList();
    // 词－权重
    public static Map<String, Double> wordWeightMap = Maps.newHashMap();
    // 字－code
    public static Map<Character, Integer> characterCodeMap = Maps.newHashMap();


    /**
     * 读取数据
     */
    static BufferedReader bufferedReader = null;
    public static void getTheData(){
        System.out.println("文件位置：" + fileName);
        // －－－－－词  词－权重
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
            String content;

            while ((content = bufferedReader.readLine()) != null){
                // 除去不符合格式要求的数据
                String[] line = content.split(",");
                if (content.equals("") || line.length != 2){
                    continue;
                }

                try {
                    double weight = Double.parseDouble(line[1]);
                    if (!allWordList.contains(line[0])) {
                        allWordList.add(line[0]);
                        wordWeightMap.put(line[0], weight);
                    }
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    logger.error(e.getMessage());
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            try {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
            }catch (Exception e){
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }


        // －－－－－字－code
        int code = 0;
        for (String str : allWordList){
            char[] charArr = str.toCharArray();
            for (char ch : charArr){
                if(characterCodeMap.get(ch) == null){
                    characterCodeMap.put(ch, code++);
                }
            }
        }
    }
}

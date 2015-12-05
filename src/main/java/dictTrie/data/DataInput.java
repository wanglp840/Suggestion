package dictTrie.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.org.apache.xerces.internal.xs.StringList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

/**
 * @Auther wanglp
 * @Time 15/11/23 上午12:11
 * @Email wanglp840@nenu.edu.cn
 */

public class DataInput {
    private static String  tmpFileName = DataInput.class.getClassLoader().getResource("test1").getFile();

    // 词
    public static List<String> allStrList = Lists.newArrayList();

    // 词－权重
    public static Map<String, Double> weightMap = Maps.newHashMap();
    // 字－code
    public static Map<Character, Integer> strCode = Maps.newHashMap();


    static BufferedReader bufferedReader = null;
    public static void getTheData(){
        // －－－－－词  词－权重
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(tmpFileName)));
            String content;

            while ((content = bufferedReader.readLine()) != null){
                // 除去不符合格式要求的数据
                String[] line = content.split(",");
                if (content.equals("") || line.length != 2){
                    continue;
                }

                try {
                    double weight = Double.parseDouble(line[1]);
                    if (!allStrList.contains(line[0])) {
                        allStrList.add(line[0]);
                        weightMap.put(line[0], weight);
                    }
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        // －－－－－字－code
        int code = 0;
        for (String str : allStrList){
            char[] charArr = str.toCharArray();
            for (char ch : charArr){
                if(strCode.get(ch) == null){
                    strCode.put(ch, code++);
                }
            }
        }
    }
}

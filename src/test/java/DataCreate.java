import org.junit.Test;

import java.io.*;
import java.util.Random;

/**
 * @Auther wanglp
 * @Time 16/1/2 下午4:47
 * @Email wanglp840@nenu.edu.cn
 */

public class DataCreate {

    @Test
    public void createDate(){
        String fileName = "/Users/wanglp/Downloads/dict_with_py.txt";
        //DataCreate.class.getClassLoader().getResource("test_all").getFile();
        BufferedReader bufferedReader = null;
        FileWriter fileWriter = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
            fileWriter = new FileWriter("/Users/wanglp/Downloads/newline.txt");
            String line;

            while ((line = bufferedReader.readLine()) != null){
                String[] tmp = line.split(" ");

                // 重新拼接新的一行数据格式
                String jianpin = "";
                String newLine = tmp[0];
                newLine = newLine + ",";
                for (int i = 1; i < tmp.length; i++){
                    newLine = newLine + tmp[i];
                    jianpin = jianpin + tmp[i].charAt(0);
                }
                newLine = newLine + ",";
                newLine = newLine + jianpin + ",";

                String weight = new Random().nextInt(100)+"";
                newLine = newLine + weight;


                // 写数据
                fileWriter.write(newLine);
                fileWriter.write("\n");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fileWriter != null){
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

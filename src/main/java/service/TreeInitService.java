package service;

import com.google.common.collect.Maps;
import common.entity.Node;
import common.other.PropertiesConstants;
import common.other.TreeUsedData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 * @Auther wanglp
 * @Time 15/12/3 下午6:55
 * @Email wanglp840@nenu.edu.cn
 */

@Service("treeInitService")
public class TreeInitService {

    @Autowired
    private TreeService treeService;

    // 日志类
    Logger logger = Logger.getLogger(TreeInitService.class.getName());
    // 文件版本号
    static long fileVersion = 0;


    // -----------------字典树的创建和更新启动
    @PostConstruct
    public void init() {
        System.out.println("postConstruct－－－－－－－－－红红火火－－－－－－－");

        // 树创建更新线程启动
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        // 文件
                        String fileName = TreeService.class.getClassLoader().getResource(PropertiesConstants.dataSourceFileName).getFile();
                        long tmpVersion = new File(fileName).lastModified();
                        if (tmpVersion != fileVersion) {
                            // 生成树 更新版本号
                            buildTheTree(fileName);
                            fileVersion = tmpVersion;
                        }
                        System.out.println("睡眠中");
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    }
                }
            }
        }).start();
    }

    private void buildTheTree(String fileName){

        long beginTime = System.currentTimeMillis();
        logger.error("文件有变化，正在重新生成新的树");
        buildTheTreeT(fileName);
        long endTime = System.currentTimeMillis();
        logger.error("树更新完毕,耗时(毫秒)：" + (endTime - beginTime));
    }

    private void buildTheTreeT(String fileName){
        TreeUsedData treeUsedData = new TreeUsedData();
        BufferedReader bufferedReader = null;
        String content;

        // 字－code   letter－code
        int chCode = 0;
        Map<Character, Integer> characterCodeMap = Maps.newHashMap();
        char[] abc = "abcdefgfhijklmnopqrstuvwxyz".toCharArray();
        for (char tmp:abc){
            characterCodeMap.put(tmp, chCode++);
        }


        try {
            bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
            Node rootNode = new Node(-1, "");
            while ((content = bufferedReader.readLine()) != null){
                // 除去不符合格式要求的数据
                String[] line = content.split(",");
                if (content.equals("") || line.length != 4){
                    continue;
                }
                // 字－code
                char[] charArr = line[0].toCharArray();
                for (char ch : charArr){
                    if(characterCodeMap.get(ch) == null){
                        characterCodeMap.put(ch, chCode++);
                    }
                }

                try {
                    double weight = Double.parseDouble(line[3]);
                    // 插入word 包括全拼 简拼
                    treeService.insertWordToTree(rootNode, line[0],line[1],line[2], weight, characterCodeMap);
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error("类型转换或者插入树节点出错" + e.getMessage());
                    continue;
                }
            }

            //  更新树
            treeUsedData.setRootNode(rootNode);
            treeUsedData.setCharacterCodeMap(characterCodeMap);
            treeService.setDataUsed(treeUsedData);

            treeService.preOrderDisplayTreeNode(treeUsedData.getRootNode());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("建树失败，" + e.getMessage());
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
    }
}

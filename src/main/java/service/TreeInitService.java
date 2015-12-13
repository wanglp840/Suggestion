package service;

import common.data.DataUtils;
import common.entity.Node;
import common.other.PropertiesConstants;
import common.other.TreeUsedData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

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
                        // 文件版本号
                        String dataFile = TreeService.class.getClassLoader().getResource(PropertiesConstants.dataSourceFileName).getFile();
                        long tmpVersion = new File(dataFile).lastModified();
                        if (tmpVersion != fileVersion) {
                            // 生成树 更新版本号
                            buildTheTree();
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

    private void buildTheTree(){
        TreeUsedData treeUsedData = new TreeUsedData();
        // 数据获取
        DataUtils.getTheData();

        long beginTime = System.currentTimeMillis();
        logger.error("文件有变化，正在重新生成新的树");

        // 创建一颗树
        Node rootNode = new Node(-1, " ");
        rootNode = treeService.buildDictTree(DataUtils.allWordList, rootNode, DataUtils.wordWeightMap, DataUtils.characterCodeMap);

        // 设置当前树使用的数据
        treeUsedData.setAllWordList(DataUtils.allWordList);
        treeUsedData.setWordWeightMap(DataUtils.wordWeightMap);
        treeUsedData.setCharacterCodeMap(DataUtils.characterCodeMap);
        treeUsedData.setRootNode(rootNode);
        treeService.setDataUsed(treeUsedData);

        long endTime = System.currentTimeMillis();
        logger.error("树更新完毕,耗时(毫秒)：" + (endTime-beginTime));
    }
}

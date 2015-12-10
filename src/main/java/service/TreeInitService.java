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

    Logger logger = Logger.getLogger(TreeInitService.class.getName());

    // -----------------字典树的创建和更新启动
    @PostConstruct
    public void init() {

        System.out.println("postConstruct－－－－－－－－－红红火火－－－－－－－");
        final TreeUsedData treeUsedData = new TreeUsedData();

        // 数据获取
        DataUtils.getTheData();

        // 创建一颗树
        Node rootNode = new Node(-1, " ");
        rootNode = treeService.buildDictTree(DataUtils.allWordList, rootNode, DataUtils.wordWeightMap, DataUtils.characterCodeMap);

        // 设置当前树使用的数据
        treeUsedData.setAllWordList(DataUtils.allWordList);
        treeUsedData.setWordWeightMap(DataUtils.wordWeightMap);
        treeUsedData.setCharacterCodeMap(DataUtils.characterCodeMap);
        treeUsedData.setRootNode(rootNode);

        treeService.setDataUsed(treeUsedData);

        // 树更新操作
        new Thread(new Runnable() {
            public void run() {
                // 文件版本号
                long fileVersion = new File(TreeInitService.class.getClassLoader().getResource(PropertiesConstants.dataSourceFileName).getFile()).lastModified();

                while (true) {
                    try {
                        // 文件更新版本号
                        long fileUpdatedVersion = new File(TreeService.class.getClassLoader().getResource(PropertiesConstants.dataSourceFileName).getFile()).lastModified();
                        if (fileUpdatedVersion != fileVersion) {
                            System.out.println("文件有变化，正在重新生成新的树");

                            // 获取新的数据
                            DataUtils.getTheData();

                            // 创建新的树
                            Node newRootNode = new Node(-1, "");
                            newRootNode = treeService.buildDictTree(DataUtils.allWordList, newRootNode, DataUtils.wordWeightMap, DataUtils.characterCodeMap);

                            // 更新当前树使用的数据
                            treeUsedData.setAllWordList(DataUtils.allWordList);
                            treeUsedData.setWordWeightMap(DataUtils.wordWeightMap);
                            treeUsedData.setCharacterCodeMap(DataUtils.characterCodeMap);
                            treeUsedData.setRootNode(newRootNode);

                            // 更新文件编号
                            fileVersion = fileUpdatedVersion;
                        }
                        System.out.println("睡眠中");
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    }
                }
            }
        }).start();
    }
}

package dictTrie.treeT;

import dictTrie.data.DataInput;
import dictTrie.data.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @Auther wanglp
 * @Time 15/12/3 下午6:55
 * @Email wanglp840@nenu.edu.cn
 */

@Service
public class TreeInit {

    @Autowired
    private TreeService treeService;


    // ---字典树的创建和更新启动
    @PostConstruct
    public void init(){

        System.out.println("postConstruct 启动－－－－－－－－－红红火火－－－－－－－");
        final DataUtils dataUtils = new DataUtils();

        // 数据获取
        DataInput.getTheData();

        // 创建一颗树
        Node rootNode = new Node(-1, " ");
        rootNode = treeService.buildDictTree(DataInput.allStrList, rootNode, DataInput.weightMap, DataInput.strCode);

        // 设置当前树使用的数据
        dataUtils.setAllStrList(DataInput.allStrList);
        dataUtils.setWeightMap(DataInput.weightMap);
        dataUtils.setStrCode(DataInput.strCode);
        dataUtils.setRootNode(rootNode);

        treeService.setDataUsed(dataUtils);

        // 树更新操作
        new Thread(new Runnable() {
            public void run() {
                long fileVersion = new File(TreeService.class.getClassLoader().getResource("test1").getFile()).lastModified();

                while (true) {
                    try {
                        long fileNewVersion = new File(TreeService.class.getClassLoader().getResource("test1").getFile()).lastModified();
                        if (fileNewVersion != fileVersion) {
                            System.out.println("文件有变化，正在重新生成新的树");

                            // 获取新的数据
                            DataInput.getTheData();

                            // 创建新的树
                            Node root = new Node(-1, "");
                            root = treeService.buildDictTree(DataInput.allStrList, root, DataInput.weightMap, DataInput.strCode);

                            // 更新当前树使用的数据
                            dataUtils.setAllStrList(DataInput.allStrList);
                            dataUtils.setWeightMap(DataInput.weightMap);
                            dataUtils.setStrCode(DataInput.strCode);
                            dataUtils.setRootNode(root);

                            // 更新文件编号
                            fileVersion = fileNewVersion;
                        }
                        System.out.println("睡眠中");
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}

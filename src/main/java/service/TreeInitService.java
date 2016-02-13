package service;

import cache.TreeCache;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.Node;
import pojo.Rule;
import utils.Constants;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Auther wanglp
 * @Time 15/12/3 下午6:55
 * @Email wanglp840@nenu.edu.cn
 */

@Service("treeInitService")
@Slf4j
public class TreeInitService {

    @Autowired
    private TreeService treeService;

    // 文件版本号
    static long fileVersion = 0;


    // -----------------字典树的创建和更新启动
    @PostConstruct
    public void init() {
        log.info("postConstruct－－－－－－－－－红红火火－－－－－－－");

        // 树创建更新线程启动
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {

                        // 文件
                        URL url = TreeService.class.getClassLoader().getResource(Constants.dataSourceFileName);
                        if (url == null){
                            log.error("读取文件失败");
                            break;
                        }

                        String fileName = url.getFile();
                        if (StringUtils.isEmpty(fileName)){
                            continue;
                        }

                        long tmpVersion = new File(fileName).lastModified();
                        if (tmpVersion != fileVersion) {
                            // 生成树 更新版本号
                            buildTheTree(fileName);
                            fileVersion = tmpVersion;
                        }

                        log.info("睡眠中");
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                }
            }
        }).start();
    }

    private void buildTheTree(String fileName){
        Stopwatch stopwatch = Stopwatch.createStarted();

        // 建树
        log.info("文件有变化，正在重新生成新的树");
        build(fileName);

        log.info(Joiner.on("\n").join(treeService.treeCache.getNodeList()));
        log.info(" \n 树的层数为：" + treeService.treeCache.getNodeList().size());
        log.info("\n@@@@@@@@@@@@@@@@@@@@@@\n");

        // 设置节点匹配字节点的ruleList
        treeService.setNodeRuleList(treeService.treeCache.getNodeList().get(0).get(0), 0);

        log.info(Joiner.on("\n").join(treeService.treeCache.getNodeList()));
        log.info(Joiner.on("\n").withKeyValueSeparator("=>").join(treeService.treeCache.getCharacterCodeMap()));

        log.error("树更新完毕,耗时(毫秒)：" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * 读取文件建树
     */
    private void build(String fileName){
        TreeCache treeCache = new TreeCache();

        // 字编码 结点 rule信息
        int ruleId = 0;
        Map<Character, Integer> characterCodeMap = Maps.newHashMap();
        List<List<Node>> allLevelNodeList = Lists.newArrayList();
        List<Rule> allRuleList = Lists.newArrayList();

        // 字母－code信息存储
        int chCode = 0;
        String abcStr = "abcdefgfhijklmnopqrstuvwxyz";
        for (char tmp : abcStr.toCharArray()){
            characterCodeMap.put(tmp, chCode++);
        }

        BufferedReader bufferedReader = null;
        String content;
        try {
            Node rootNode = new Node(0);
            List<Node> rootList = Lists.newArrayList();
            rootList.add(rootNode);
            allLevelNodeList.add(rootList);

            bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
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
                    // 插入word 包括全拼 简拼
                    treeService.insertWordToTree(allLevelNodeList, line[0], line[1], line[2], ruleId, characterCodeMap);

                    double weight = Double.parseDouble(line[3]);
                    Rule rule = new Rule(ruleId, line[0]+ "，" + line[1] + "，" + line[2] + "，"+ weight, weight);
                    allRuleList.add(rule);
                    ruleId++;
                }catch (Exception e){
                    e.printStackTrace();
                    log.error("类型转换或者插入树节点出错" + e.getMessage());
                }
            }

            //  更新树
            treeCache.setCharacterCodeMap(characterCodeMap);
            treeCache.setNodeList(allLevelNodeList);
            treeCache.setRuleList(allRuleList);
            treeService.setDataUsed(treeCache);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("建树失败，" + e.getMessage());
        } finally {
            try {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
            }catch (Exception e){
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }
}

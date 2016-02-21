package com.suggestion.service;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.suggestion.cache.TreeCache;
import com.suggestion.pojo.Rule;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Auther wanglp
 * @Time 15/12/3 下午6:55
 * @Email wanglp840@nenu.edu.cn
 */

@Service
@Log4j2
public class IndexUpdateService {

    @Autowired
    private IndexService indexService;

    @Autowired
    private String dataSourceFileName;

    // -----------------字典树的创建和更新启动
    @PostConstruct
    public void init() {
        log.info("postConstruct");

        // 树创建更新线程启动
        new Thread(new Runnable() {
            public void run() {
                // 文件版本号
                long fileVersion = 0;
                while (true) {
                    try {

                        // 文件
                        URL url = IndexService.class.getClassLoader().getResource(dataSourceFileName);
                        if (url == null) {
                            log.error("读取文件失败");
                            break;
                        }

                        String fileName = url.getFile();
                        if (StringUtils.isEmpty(fileName)) {
                            continue;
                        }

                        long tmpVersion = new File(fileName).lastModified();
                        if (tmpVersion != fileVersion) {
                            // 生成树 更新版本号
                            loadFromFile(fileName);
                            fileVersion = tmpVersion;
                        }

                        log.info("睡眠中");
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        log.error("加载词典文件出现异常", e);
                        break;
                    }
                }
            }
        }).start();
    }

    private void loadFromFile(String fileName) {

        log.info("文件有变化，正在重新生成新的树");
        Stopwatch stopwatch = Stopwatch.createStarted();

        // 建树
        TreeCache treeCache = buildTreeCache(fileName);

        //搜索树缓存设置
        indexService.setTreeCache(treeCache);

        recordLog(stopwatch, treeCache);
    }

    /**
     * 读取文件建树
     */
    private TreeCache buildTreeCache(String fileName) {


        TreeCache treeCache = TreeCache.builder().build();
        treeCache.init();

        List<Rule> allRuleList = Lists.newArrayList();
        FileReader fileReader = null;
        try {


            fileReader = new FileReader(new File(fileName));
            LineIterator it = IOUtils.lineIterator(fileReader);
            while (it.hasNext()) {
                String content = it.nextLine();
                String[] line = normalizeLine(content);
                if (line == null) continue;
                // 插入word 包括全拼 简拼
                int ruleId = treeCache.insertLineToTree(line);
            }

            // 设置节点匹配字节点的ruleList
            treeCache.procNodeMatchRuleList();

        } catch (Exception e) {
            log.error("建树失败，", e);
        } finally {
            Closeables.closeQuietly(fileReader);
        }

        return treeCache;
    }

    //每行数据归一化处理， 不符合要求的过滤掉
    private String[] normalizeLine(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        String[] line = content.split(",");
        if (line.length != 4) {
            log.error("字典数据中有不合法数据{}", line);
            return null;
        }

        double weight;
        try {
            weight = Double.parseDouble(line[3]);
        } catch (Exception e) {
            log.error("字典数据中有不合法数据{}", content);
            return null;
        }

        return line;
    }

    private void recordLog(Stopwatch stopwatch, TreeCache treeCache) {
        log.info(Joiner.on("\n").join(treeCache.getNodeList()));
        log.info(" \n 树的层数为：" + treeCache.getNodeList().size());

        log.info(Joiner.on("\n").withKeyValueSeparator("=>").join(treeCache.getCharacterCodeMap()));
        log.error("树更新完毕,耗时(毫秒)：" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

}

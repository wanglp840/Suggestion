package com.suggestion.web.controller;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.suggestion.service.IndexService;
import com.suggestion.utils.Constants;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Auther wanglp
 * @Time 15/12/1 下午6:17
 * @Email wanglp840@nenu.edu.cn
 */

@Log4j2
@Controller
@RequestMapping(value = Constants.INDEX_SEARCH)
public class SearchController {

    @Autowired
    private IndexService indexService;

    @RequestMapping(value = "")
    @ResponseBody
    public JSONObject search(String queryWord) {
        List<String> resultList = Lists.newArrayList();


        // 处理后的查询串
        queryWord = indexService.getTheHandledQuery(queryWord);
        if (!queryWord.equals("")) {

            log.info("查询检索开始!");
            Stopwatch stopwatch = Stopwatch.createStarted();

            resultList = indexService.search(queryWord);

            log.info("检索完毕,耗时(毫秒):" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }


        // JsonArr生成
        JSONArray jsonArray = new JSONArray();
        for (String tmp : resultList){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("strContent", tmp);
            jsonObject.put("handledQuery", queryWord);
            jsonArray.add(jsonObject);
        }

        // 返回Json
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }
}

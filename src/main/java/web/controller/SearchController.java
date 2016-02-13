package web.controller;

import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.TreeService;
import utils.Constants;

import java.util.List;

/**
 * @Auther wanglp
 * @Time 15/12/1 下午6:17
 * @Email wanglp840@nenu.edu.cn
 */


@Controller
@RequestMapping(value = Constants.INDEX_SEARCH)
public class SearchController {

    @Autowired
    private TreeService treeService;

    @RequestMapping(value = "")
    @ResponseBody
    public JSONObject search(String queryWord) {
        List<String> resultList = Lists.newArrayList();

        // 处理后的查询串
        queryWord = treeService.getTheHandledQuery(queryWord);
        if (!queryWord.equals("")) {
            resultList = treeService.prefixWordTopList(queryWord);
        }


        // JsonArr生成
        JSONArray jsonArray = new JSONArray();
        for (String tmp : resultList){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("strContent", tmp);
            jsonObject.put("handledQuery", queryWord);
            // 计算标红位置
            int[] position = treeService.getBoldPosition(tmp, queryWord);
            jsonObject.put("startPos", position[0]);
            jsonObject.put("endPos", position[1]);
            jsonArray.add(jsonObject);
        }

        // 返回Json
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }
}

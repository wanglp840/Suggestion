package web.controller;

import com.google.common.collect.Lists;
import common.other.URLConstants;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.TreeService;

import java.util.List;

/**
 * @Auther wanglp
 * @Time 15/12/1 下午6:17
 * @Email wanglp840@nenu.edu.cn
 */


@Controller
@RequestMapping(value = URLConstants.INDEX_SEARCH)
public class SearchController {

    @Autowired
    private TreeService treeService;

    @RequestMapping(value = "")
    @ResponseBody
    public JSONObject search(String queryWord) {
        List<String> words = Lists.newArrayList();
        if (!queryWord.equals("")) {
            words = treeService.prefixWordTopList(queryWord);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("data", words.toArray());
        return jsonObject;
    }


}

package web.controller;

import com.google.common.collect.Lists;
import dictTrie.enums.SearchType;
import dictTrie.other.URLConstants;
import dictTrie.treeT.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    private TreeService tree;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String search(String q, Model model){
        List<String> words = Lists.newArrayList();

        if (!q.equals("")){
            words = tree.prefixWordsTop(q, SearchType.SEARCH_TOP, 10);

            model.addAttribute("queryWords", words);
        }

        return "index";
    }


}

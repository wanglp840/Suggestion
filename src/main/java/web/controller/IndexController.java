package web.controller;

import common.other.URLConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Auther wanglp
 * @Time 15/12/4 下午10:57
 * @Email wanglp840@nenu.edu.cn
 */

@Controller
@RequestMapping(value = URLConstants.INDEX)
public class IndexController {

    @RequestMapping(value = {"","index"}, method = RequestMethod.GET)
    public String toIndex(){
        return "index";
    }
}

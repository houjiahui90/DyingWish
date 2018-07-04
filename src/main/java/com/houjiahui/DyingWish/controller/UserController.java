package com.houjiahui.DyingWish.controller;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import com.houjiahui.DyingWish.entity.User;
import com.houjiahui.DyingWish.service.UserService;
import com.houjiahui.core.controller.BaseController;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping("/showUser.do")
    public User selectUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String parmar = request.getParameter("id");
        if (null == parmar){
            parmar = "1";
        }
        String userId = parmar;
        logger.debug("收到了参数{}",parmar);
        User user = this.userService.selectUser(userId);
        if (null != user) {
            return user;
        }else {
            return new User();
        }
    }
}

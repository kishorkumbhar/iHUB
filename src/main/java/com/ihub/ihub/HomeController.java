package com.ihub.ihub;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController {
    @RequestMapping ("/public")
    public String home(){
        return "index";
    }
}

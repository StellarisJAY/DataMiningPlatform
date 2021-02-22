package com.jay.dataprocessing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/home")
    public String homePage(){
        return "home";
    }

    @GetMapping("/bayes")
    public String bayesPage(){
        return "bayes";
    }

    @GetMapping("/apriori")
    public String aprioriPage(){
        return "apriori";
    }

    @GetMapping("/kmeans")
    public String kmeans(){
        return "kmeans";
    }
}

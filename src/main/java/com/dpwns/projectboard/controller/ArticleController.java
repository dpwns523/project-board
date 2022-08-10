package com.dpwns.projectboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * /articles/{article-id}
 * /articles/search
 * /articles/search-hashtag
 */
@RequestMapping("/articles")
@Controller
public class ArticleController {

    @GetMapping
    public String articles(ModelMap map){
        map.addAttribute("articles", List.of());
        return "articles/index";
    }

    @GetMapping("/{article-id}")
    public String article(@PathVariable(name = "article-id") Long articleId, ModelMap map){
        map.addAttribute("article", "article");     // TODO: 구현할 때 실제 데이터를 넣어야함
        map.addAttribute("articleComments", List.of());
        return "articles/detail";
    }

}

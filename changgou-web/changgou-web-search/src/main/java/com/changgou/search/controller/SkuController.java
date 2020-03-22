package com.changgou.search.controller;

import com.changgou.search.feign.SkuFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * changgou-web-search 实现调用搜索的控制层
 */
@Controller
@RequestMapping(value = "/search")
public class SkuController {
    @Autowired
    private SkuFeign skuFeign; //不要导错包！ com.changgou.search.feign.SkuFeign; 不是goods里那个批量导入的

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/list")
    public String search(@RequestParam(required = false) Map searchMap, Model model){
        //调用changgou-service-search微服务
        Map resultMap = skuFeign.search(searchMap);
        model.addAttribute("result",resultMap);
        model.addAttribute("searchMap",searchMap);
        return "search";
    }


}

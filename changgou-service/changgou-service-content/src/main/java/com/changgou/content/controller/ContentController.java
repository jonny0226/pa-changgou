package com.changgou.content.controller;

import com.changgou.content.pojo.Content;
import com.changgou.entity.Result;
import com.changgou.content.service.ContentService;
import com.changgou.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 广告信息的controller层
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentService contentService;


    /**
     * 根据分类id查询广告集合
     * @param id
     * @return
     */
    @GetMapping("/list/category/{id}")
    public Result<List<Content>> findByCategory(@PathVariable(name = "id") Long id){
        //根据分类id查询广告的集合
        List<Content> contentList =  contentService.findByCategory(id);
        return new Result<List<Content>>(true, StatusCode.OK,"根据分类id查询广告信息集合成功！",contentList);
    }


    /***
     * 新增Content数据
     * @param content
     * @return
     */
    @PostMapping
    public Result add(@RequestBody   Content content){
        //调用ContentService实现添加Content
        contentService.add(content);
        return new Result(true,StatusCode.OK,"添加成功");
    }
}

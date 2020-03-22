package com.changgou.content.service.impl;

import com.changgou.content.dao.ContentMapper;
import com.changgou.content.pojo.Content;
import com.changgou.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 根据分类id查询广告的集合的接口实现类
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired(required = false)
    private ContentMapper contentMapper;

    /***
     * 根据分类ID查询
     * @param id
     * @return
     */
    @Override
    public List<Content> findByCategory(Long id) {
        Content content = new Content();
        content.setCategoryId(id);
        content.setStatus("1");
        return contentMapper.select(content);
    }

    /**
     * 增加Content
     * @param content
     */
    @Override
    public void add(Content content){
        contentMapper.insert(content);
    }
}

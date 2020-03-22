package com.changgou.content.service;

import com.changgou.content.pojo.Content;

import java.util.List;

/**
 * 广告的接口层
 */
public interface ContentService {

    /**
     * 根据分类id查询广告的列表
     * @param id
     * @return
     */
    List<Content> findByCategory(Long id);

    /**
     * add
     * @param content
     */
    void add(Content content);
}

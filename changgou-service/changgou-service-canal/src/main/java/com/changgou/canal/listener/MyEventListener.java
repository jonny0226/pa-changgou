package com.changgou.canal.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.changgou.entity.Result;
import com.xpand.starter.canal.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * canal监听程序 用作广告的数据在数据库修改之后的监听
 */
@CanalEventListener // 该类监听数据的变化
public class MyEventListener {



//    @InsertListenPoint//新增
//    public void onEvent(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
//        //do something...
//    }
//
//    @UpdateListenPoint//更新
//    public void onEvent1(CanalEntry.RowData rowData) {
//        //do something...
//        //更新前的数据
//        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
//        for (CanalEntry.Column column : beforeColumnsList) {
//            System.out.println("列名："+column.getName()+"列值："+column.getValue());
//        }
//        System.out.println("=================================================================");
//        //更新后的数据
//        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
//        for (CanalEntry.Column column : afterColumnsList) {
//            System.out.println("更新后列名："+column.getName()+"更新后列值："+column.getValue());
//        }
//    }
//
//    @DeleteListenPoint
//    public void onEvent3(CanalEntry.EventType eventType) {
//        //do something...
//    }



    @Autowired(required = false)
    private ContentFeign contentFeign;

    //字符串  ？？？？？？？？？？？？？
    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    //自定义数据库的操作来监听
    @ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content", "tb_content_category"},  eventType = {
            CanalEntry.EventType.UPDATE,
            CanalEntry.EventType.DELETE,
            CanalEntry.EventType.INSERT})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //1获取列名为 category_id的值
        String categoryId = getColumnValue(eventType, rowData);
        //2 调用feign 获取该分类下所有的广告集合
        Result<List<Content>> categoryresult = contentFeign.findByCategory(Long.valueOf(categoryId));
        List<Content> data = categoryresult.getData();
        //3 使用redisTemplate存储到redis中
        stringRedisTemplate.boundValueOps("content_"+categoryId).set(JSON.toJSONString(data));

    }

    //类中定义的方法 在本类中直接试用贴方法名 不用使用对象调用
    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //1.判断更改类型 如果是删除 则需要获取到before的数据
        String categoryId = "";
        if (CanalEntry.EventType.DELETE == eventType) {
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                //column.getName(列的名称   column.getValue() 列对应的值
                if (column.getName().equals("category_id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        } else {
            //2判断是 更新 新增 获取after的数据
            List<CanalEntry.Column> beforeColumnsList = rowData.getAfterColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                //column.getName(列的名称   column.getValue() 列对应的值
                if (column.getName().equals("category_id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        }
        //3.返回
        return categoryId;
    }


    //判断 如果是删除  则获取beforlist
    //判断 如果是添加 或者是更新 获取afterlist
}
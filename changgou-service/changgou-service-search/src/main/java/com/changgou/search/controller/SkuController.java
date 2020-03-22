package com.changgou.search.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailParseException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * sku库存商品的控制层
 */
@RestController
@RequestMapping(value = "/search")
public class SkuController {

    @Autowired(required = false)
    private SkuService skuService;

    /**
     * 导入数据
     *
     * @return
     */
    public Result search() {
        skuService.importSku();
        return new Result(true, StatusCode.OK, "search微服务向es中导入数据成功！");
    }

    /**
     * 搜索框中按照一定条件搜索数据
     * @param searchMap
     * @return
     *
     *   @GetMapping
    Map search(@RequestParam(required = false) Map searchMap);//RequestParam?? 传过来的参数可以不写？
     */
    @GetMapping
    public Map search(@RequestParam(required = false)Map searchMap) {
        //1接收参数 Map类型的参数 用于封装数据
        //2 使用es执行查询
        //3 返回查询结果
       return skuService.search(searchMap);

    }

}

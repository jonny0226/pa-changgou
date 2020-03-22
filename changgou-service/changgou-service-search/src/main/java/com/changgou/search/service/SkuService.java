package com.changgou.search.service;

import java.util.Map; /**
 * sku库存商品的service层
 */
public interface SkuService {

    /***
     * 导入SKU数据
     */
    void importSku();


    /**
     * 搜索框中按照一定条件搜索数据
     * @param searchMap
     * @return
     */
    Map search(Map<String,String> searchMap);


}

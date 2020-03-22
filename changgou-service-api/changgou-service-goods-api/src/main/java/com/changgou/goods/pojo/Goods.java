package com.changgou.goods.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0 保存商品数据的时候，需要保存Spu和Sku，一个Spu对应多个Sku，我们可以先构建一个Goods对象
 * @package com.changgou.goods.pojo *
 * @since 1.0
 */
public class Goods implements Serializable {
    private Spu spu; //spu 和sku是1对多的关系
    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "spu=" + spu +
                ", skuList=" + skuList +
                '}';
    }
}

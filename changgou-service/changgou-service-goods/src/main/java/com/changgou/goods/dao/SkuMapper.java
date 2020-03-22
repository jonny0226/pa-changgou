package com.changgou.goods.dao;
import com.changgou.goods.pojo.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:admin
 * @Description:Sku的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface SkuMapper extends Mapper<Sku> {


    //feign调用 处理订单新增之后 库存的减少
    //相当于执行update的语句
    @Update(value = "update tb_sku set num=num-#{num} where id=#{id} and num>=#{num}")
    int decCount(@Param(value = "id") Long id, @Param(value = "num") Integer num);
}

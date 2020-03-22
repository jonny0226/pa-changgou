package com.changgou.goods.dao;
import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:Brand的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface BrandMapper extends Mapper<Brand> {
    //自己定义自己的业务的sql
    // select tbb.* from tb_brand tbb, tb_category_brand tbc where category_id=76 and  tbc.brand_id=tbb.id
    @Select(value="select tbb.* from tb_brand tbb, tb_category_brand tbc where category_id=#{id} and  tbc.brand_id=tbb.id")
    List<Brand> findBrandByCategory(Integer id);




}

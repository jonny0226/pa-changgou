package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchResultMapperImpl implements SearchResultMapper{
    //自定义 数据从es中获取出来 映射给pojo 还需要非高亮数据
    //写这个方法的目的是为了获取高亮的数据
    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

        //1 获取当前页的记录集合
        List<T> content = new ArrayList<>();

        if (searchResponse.getHits() ==null || searchResponse.getHits().getTotalHits() <= 0){
            return new AggregatedPageImpl(content);//没有查询结果就返回一个空值
        }
        //2 获取并设置分页的条件

        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {    //hit就是一行一行的数据 json 这里获取到的是非高亮的数据
            String sourceAsString = hit.getSourceAsString();//只需要把数据中的source部分获取出来
            SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);//转换成pojo数据 之后再放到content中


            //获取高亮数据 将高亮的数据设置到name字段中 再返回给页面
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();//可以是很多子字段高亮 所以是map

            HighlightField highlightField = highlightFields.get("name");
            if (highlightField != null) {
                Text[] fragments = highlightField.getFragments();//高亮的碎片
                StringBuffer stringBuffer = new StringBuffer();//这个是用来拼接字符串用的
                for (Text fragment : fragments) {
                    String string = fragment.string();//高亮的碎片值
                    stringBuffer.append(string);//高亮所有的值 都拼起来了
                }
                skuInfo.setName(stringBuffer.toString()); //设置高亮的值到POJO中
            }
            content.add((T) skuInfo);//类型转换为T 给content中把skuinfo加进去
        }
        //3 获取到命中的总记录数 并设置进去
        long totalHits = hits.getTotalHits();
        //4 获取聚合的结果集 并设置进去
        Aggregations aggregations = searchResponse.getAggregations();
        //5 获取并设置游标的id
        String scrollId = searchResponse.getScrollId();
        return new AggregatedPageImpl<T>(content,pageable,totalHits,aggregations,scrollId);
    }
}

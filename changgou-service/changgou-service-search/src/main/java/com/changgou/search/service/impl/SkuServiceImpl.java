package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * sku的服务实现层 这层主要负责将获取到的数据导入到es库中 供用户调用
 */
@Service
public class SkuServiceImpl implements SkuService {


    @Autowired(required = false)
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    /**
     * 导入sku数据到es
     */
    @Override
    public void importSku() {
        //调用changgou-service-goods微服务 changgou-service-goods这个微服务将通过上架状态查询数据库中的数据被调用回来
        Result<List<Sku>> skulListResult = skuFeign.findByStatus("1");//feign就是服务之间调用来使用的 所以这可以通过feign查询状态为指定1的所以sku
        //将数据转成search.Sku
        List<Sku> data = skulListResult.getData();//这里得到的是list集合
        String skuInfoString = JSON.toJSONString(data);//这里是需要将集合先转换为json串
        List<SkuInfo> skuInfos = JSON.parseArray(skuInfoString, SkuInfo.class);//这个集合中的数据都是json格式

        for (SkuInfo skuInfo : skuInfos) {
            Map<String, Object> specMap = JSON.parseObject(skuInfo.getSpec());//??????
            skuInfo.setSpecMap(specMap);//????????
        }
        skuEsMapper.saveAll(skuInfos);
    }


    /**
     * 搜索框中按照一定条件搜索数据 返回结果
     *
     * @param searchMap 参数组成：{keywords：“手机”，brand：“TCL”,.....}
     * @return Map
     */
    @Override
    public Map search(Map<String, String> searchMap) {//参数字段哪来的？？？ 好比keywords？？？？前端定义之后 和后端保持一致
        //1 获取关键字值
        String keywords = searchMap.get("keywords");
        // 2 判断关键字的值是否为空
        if (StringUtils.isEmpty(keywords)) {
            //如果关键字是空的 为了良好的用户体验 将返回值设置为一个默认值 比如说：华为
            keywords = "华为";
        }
        //3 创建查询对象的构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();//???NativeSearchQueryBuilder
        //4 设置查询的条件
        //设置分组条件 A:商品分类 field 就是指按照这个字段分类 后面50是设置分组结果的大小 默认是10
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(50));
        //设置分组条件 B：商品品牌
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandgroup").field("brandName").size(50));
        //设置分组条件 C:商品的规格 类似于group by field
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecgroup").field("spec.keyword").size(1000));//spec.keyword?????

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //品牌的过滤查询
        if (!StringUtils.isEmpty(searchMap.get("brand"))) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", searchMap.get("brand")));
        }
        //分类的过滤查询
        if (!StringUtils.isEmpty(searchMap.get("category"))) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName", searchMap.get("category")));
        }
        //规格的过滤查询
        for (Map.Entry<String, String> stringStringEntry : searchMap.entrySet()) {
            String key = stringStringEntry.getKey();//key包含 keywords、brand、category，spec_网络制式。。。。
            if (key.startsWith("spec_")) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", stringStringEntry.getValue()));//substring是获取spec_开头的后面的那个网络制式的内容
            }
        }

        //按照价格区间 过滤查询
        String price = searchMap.get("price");
        if (!StringUtils.isEmpty(price)) {//price 可以是0-500 也可能是 3000-*（就是3000以上）所以下面需要加一个判断
            String[] split = price.split("-");//把得到的价格按照中间“-”进行切割 得到了一个string数组  两部分 第一部分就是小的价格 第二部分是大的价格
            if (!split[1].equalsIgnoreCase("*")) {//如果切分后的第二部分不是* 就按照rangeQuery查询
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]).lte(split[1]));//0<=price<=500
//                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0],true).to(split[1],true));//0<=price<=500 两种方式任选一种
            } else {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));//如果第二部分是* 就按比第一部分大的价格查询
//                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0],true));//如果第二部分是* 就按比第一部分大的价格查询  两种方式任选一种
            }
        }

        //构建过滤查询
        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);//表示 按照括号里的条件查询

        //过滤查询 设置查询的条件 matchQuery 根据字段来进行匹配查询 先分词 再查询
        //参数1 指定要搜索的字段名
        //参数2 指定要搜索的内容
        //设置主关键字查询
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));

        //分页查询
        String pageNum1 = searchMap.get("pageNum");//从传入的searchMap参数中获取当前页页码
        Integer pageNum = 1;//初始值 默认给个1 就是第一页
        if (!StringUtils.isEmpty(pageNum1)) {//如果为空默认给1 不为空就给传入获取的值
            pageNum = Integer.parseInt(pageNum1);
        }
        Integer pageSize = 40;//我们后台将每页展示的数据规定为40行
        Pageable pageable = PageRequest.of(pageNum-1,pageSize);//参数1是当前页码 0表示第一页 第二个参数表示一页显示的数据行数
        nativeSearchQueryBuilder.withPageable(pageable);//把分页查询的条件 pageable对象传入 按照这个对象去分页查询

        //排序 order by 字段 desc
        String sortField = searchMap.get("sortField");//price
        String sortRule = searchMap.get("sortRule");// desc
        if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)) {//如果获取到的排序字段和排序顺序不为空
            SortBuilder sort = SortBuilders.fieldSort("price").order("DESC".equalsIgnoreCase(sortRule)?SortOrder.DESC:SortOrder.ASC);//创建一个排序的条件对象 三元运算符判断是升序还是降序
            nativeSearchQueryBuilder.withSort(sort);//把排序条件对象传入查询
        }

        //设置高亮的字段 设置高亮的前缀和后缀
        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("name"));
        nativeSearchQueryBuilder.withHighlightBuilder(new HighlightBuilder().preTags("<em style=\"color:red\">").postTags("</em>"));//前缀后缀

        //设置主关键字查询
        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords,"name","brandName","categoryName"));

        //5 构建查询的对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        // 6 执行查询
        AggregatedPage<SkuInfo> skuPage = elasticsearchTemplate.queryForPage(query, SkuInfo.class, new SearchResultMapperImpl());//把构建好的查询对象当做入参传入进行查询

        //获取分组结果 这里是按照 商品分类 查询
        StringTerms stringTerms = (StringTerms) skuPage.getAggregation("skuCategorygroup");

        //获取分组结果  商品品牌
        StringTerms stringTermsBrand = (StringTerms) skuPage.getAggregation("skuBrandgroup");

        //获取分组结果 商品规格数据
        StringTerms stringTermsSpec = (StringTerms) skuPage.getAggregation("skuSpecgroup");


        //调用查询商品分类的私有方法 根据分组查询商品的分类List集合
        List<String> categoryList = this.getStringsCategoryList(stringTerms);

        //调用查询商品品牌的私有方法 根据分组查询商品的品牌List集合
        List<String> brandList = this.getStringsBrandList(stringTermsBrand);

        //调用查询商品规格的私有方法 根据分组查询商品的规格Map集合
        Map<String, Set<String>> specMap = this.getStringSetMap(stringTermsSpec);

        // 7获取结果并返回
        Map resultMap = new HashMap<>();
        resultMap.put("rows", skuPage.getContent());
        resultMap.put("total", skuPage.getTotalElements());
        resultMap.put("totalPages", skuPage.getTotalPages());
        resultMap.put("categoryList", categoryList);//给返回前端的map中添加分类的集合
        resultMap.put("brandList", brandList);//给返回前端的map中添加品牌的集合
        resultMap.put("specMap", specMap);//给返回前端的map中添加品牌的集合


        return resultMap;
    }

    /**
     * 获取分类列表数据
     *
     * @param stringTerms
     * @return
     */
    private List<String> getStringsCategoryList(StringTerms stringTerms) {
        List<String> categoryList = new ArrayList<>();
        if (stringTerms != null) {
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();//分组的值
                categoryList.add(keyAsString);
            }
        }
        return categoryList;
    }


    /**
     * 获取品牌列表
     *
     * @param stringTermsBrand
     * @return
     */
    private List<String> getStringsBrandList(StringTerms stringTermsBrand) {
        List<String> brandList = new ArrayList<>();
        if (stringTermsBrand != null) {
            for (StringTerms.Bucket bucket : stringTermsBrand.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();//分组的值
                brandList.add(keyAsString);
            }
        }
        return brandList;
    }

    /**
     * 获取规格列表数据
     *
     * @param stringTermsSpec
     * @return Map<String   ,   Set   <   String>> string 是显示的规格说明 set集合中是各个规格的一个集合
     */
    private Map<String, Set<String>> getStringSetMap(StringTerms stringTermsSpec) {
        Map<String, Set<String>> specMap = new HashMap<>();
        Set<String> specList = new HashSet<>();
        if (stringTermsSpec != null) {
            for (StringTerms.Bucket bucket : stringTermsSpec.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();//分组的值
                specList.add(keyAsString);
            }
        }
        for (String specjson : specList) {
            Map<String, String> map = JSON.parseObject(specjson, Map.class);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();    //规格名字
                String value = entry.getValue();//规格选项值
                //获取当前规格名字对应的规格数据 如果这个map中没有key就是空的map
                Set<String> specValues = specMap.get(key);
                if (specValues == null) {//判断map中key值是否为空
                    specValues = new HashSet<String>();//如果set集合中的key不存在 就新建一个集合
                }
                //将当前规格加入到集合中 （之前的旧的集合中已经有这个key值，如果key存在 就直接存储，
                // 好比华为笔记本的规格5.5寸已经存在 如果再寸5.5就覆盖，如果再寸5寸的就加进去了）
                specValues.add(value);
                //将数据存入到specMap中
                specMap.put(key, specValues);
            }

        }
        return specMap;
    }


}

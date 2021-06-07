package com.gdou.mall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gdou.mall.pojo.ProductSearchParam;
import com.gdou.mall.pojo.ProductSkuInfo;
import com.gdou.mall.pojo.ProductSkuInfoSearch;
import com.gdou.mall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    JestClient jestClient;
    //查询Sku-Search
    @Override
    public List<ProductSkuInfoSearch> list(ProductSearchParam productSearchParam) {
        List<ProductSkuInfoSearch> productSkuInfoSearchList = new ArrayList<>();
        //获取dsl并执行dsl获取 Sku-Search
        String dsl = getDsl(productSearchParam);
        Search search = new Search.Builder(dsl).addIndex("mall").addType("ProductSkuInfo").build();
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取hits并解析
        List<SearchResult.Hit<ProductSkuInfoSearch, Void>> hits = searchResult.getHits(ProductSkuInfoSearch.class);
        if (hits != null) {
            for (SearchResult.Hit<ProductSkuInfoSearch, Void> hit : hits) {
                ProductSkuInfoSearch source = hit.source;
                Map<String, List<String>> highlight = hit.highlight;
                if (null != highlight && highlight.size() != 0) {
                    String skuName = highlight.get("skuName").get(0);
                    source.setSkuName(skuName);
                }
                productSkuInfoSearchList.add(source);
            }
        }

        return productSkuInfoSearchList;
    }

    //后台添加Sku，ES同步更新
    @Override
    public void updateEsInfo(ProductSkuInfo productSkuInfo) throws IOException {
        ProductSkuInfoSearch productSkuInfoSearch=new ProductSkuInfoSearch();
        BeanUtils.copyProperties(productSkuInfo,productSkuInfoSearch);
        Index put=new Index.Builder(productSkuInfoSearch).index("mall").type("ProductSkuInfo")
                .id(productSkuInfoSearch.getId()+"")
                .build();
        jestClient.execute(put);
    }

    //生成dsl语句
    private String getDsl(ProductSearchParam productSearchParam) {
        String catalog3Id = productSearchParam.getCatalog3Id();
        String[] valueIds = productSearchParam.getValueId();
        String keyword = productSearchParam.getKeyword();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //filter
        if (StringUtils.isNotBlank(catalog3Id)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        if (null != valueIds && valueIds.length != 0) {
            for (String valueId : valueIds) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        //must
        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQumeryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQumeryBuilder);
        }

        //query
        searchSourceBuilder.query(boolQueryBuilder);

        //highlight
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);

        //sort and page
        searchSourceBuilder.sort("id", SortOrder.DESC);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        return searchSourceBuilder.toString();
    }
}

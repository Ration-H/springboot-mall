package com.gdou.mall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gdou.mall.LoginRequired;
import com.gdou.mall.pojo.*;
import com.gdou.mall.service.AttrService;
import com.gdou.mall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class SearchController {
    @Reference
    SearchService searchService;

    @Reference
    AttrService attrService;

    //首页请求
    @RequestMapping("index")
    @LoginRequired(mustLogin = false)
    public String index() {
        return "index";
    }

    //商品列表请求
    @RequestMapping("list.html")
    public String list(ProductSearchParam productSearchParam, ModelMap modelMap) {
        //查询到的商品列表
        List<ProductSkuInfoSearch> productSkuInfoSearchList = searchService.list(productSearchParam);
        modelMap.put("skuLsInfoList", productSkuInfoSearchList);

        //获取平台属性
        Set<Long> valueIdSet = new HashSet<>();
        for (ProductSkuInfoSearch productSkuInfoSearch : productSkuInfoSearchList) {
            List<ProductSkuAttrValue> skuAttrValueList = productSkuInfoSearch.getSkuAttrValueList();
            for (ProductSkuAttrValue productSkuAttrValue : skuAttrValueList) {
                valueIdSet.add(productSkuAttrValue.getValueId());
            }
        }
        List<ProductBaseAttrInfo> productBaseAttrInfoList = attrService.getAttrValueListByValueId(valueIdSet);
        //将平台属性放入model
        modelMap.put("attrList", productBaseAttrInfoList);


        //为平台属性设置url
        String urlParam = getUrlParam(productSearchParam);
        modelMap.put("urlParam", urlParam);

        //删除已点击的平台属性并添加面包屑
        String[] delValueIds = productSearchParam.getValueId();
        if (delValueIds != null) {
            //面包屑数组
            List<ProductSearchCrumb> productSearchCrumbList = new ArrayList<>();

            for (String delValueId : delValueIds) {
                ProductSearchCrumb searchCrumb = new ProductSearchCrumb();
                searchCrumb.setValueId(delValueId);
                searchCrumb.setUrlParam(getUrlParam(productSearchParam, delValueId));
                productSearchCrumbList.add(searchCrumb);

                Iterator<ProductBaseAttrInfo> iterator = productBaseAttrInfoList.iterator();
                while (iterator.hasNext()) {
                    ProductBaseAttrInfo productBaseAttrInfo = iterator.next();
                    List<ProductBaseAttrValue> productBaseAttrInfoAttrValueList = productBaseAttrInfo.getAttrValueList();
                    for (ProductBaseAttrValue productBaseAttrValue : productBaseAttrInfoAttrValueList) {
                        String id = String.valueOf(productBaseAttrValue.getId());
                        if (delValueId.equals(id)) {
                            //删除平台属性时，同时为面包屑属性值 赋值
                            searchCrumb.setValueName(productBaseAttrValue.getValueName());
                            iterator.remove();
                        }
                    }
                }
            }
            modelMap.put("attrValueSelectedList", productSearchCrumbList);
        }


        return "list";
    }


    //拼装urlParam与面包屑url
    private String getUrlParam(ProductSearchParam productSearchParam, String... delValueId) {
        String catalog3Id = productSearchParam.getCatalog3Id();
        String keyword = productSearchParam.getKeyword();
        String[] valueIds = productSearchParam.getValueId();

        String urlParam = "";

        if (StringUtils.isNotBlank(catalog3Id)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam += "&";
            }
            urlParam += "catalog3Id=" + catalog3Id;
        }

        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam += "&";
            }
            urlParam += "catalog3Id=" + keyword;
        }

        if (valueIds != null && valueIds.length != 0) {
            for (String valueId : valueIds) {
                if (delValueId.length == 0) {
                    urlParam += "&valueId=" + valueId;
                } else if (delValueId.length != 0 && !valueId.equals(delValueId[0])) {
                    urlParam += "&valueId=" + valueId;
                }
            }
        }
        return urlParam;
    }
}
package com.gdou.mall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gdou.mall.pojo.ProductSkuInfo;
import com.gdou.mall.pojo.ProductSkuInfoSearch;
import com.gdou.mall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallSearchServiceApplicationTests {
    @Reference
    SkuService skuService;

    @Autowired
    JestClient jestClient;

    @Test
    public void dataTransfer() {
        fromDbToSearchDb();
    }

    private void fromDbToSearchDb() {
        //从Mysql获取所有Sku
        List<ProductSkuInfo> productSkuInfoList = skuService.getAllSku();

        //将Mysql数据转化为ES数据
        List<ProductSkuInfoSearch> productSkuInfoSearchList = new ArrayList<>(productSkuInfoList.size());

        for (ProductSkuInfo productSkuInfo:productSkuInfoList){
            ProductSkuInfoSearch productSkuInfoSearch=new ProductSkuInfoSearch();
            try {
                BeanUtils.copyProperties(productSkuInfoSearch,productSkuInfo);
                productSkuInfoSearchList.add(productSkuInfoSearch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //导入ES数据库
        for(ProductSkuInfoSearch productSkuInfoSearch:productSkuInfoSearchList){
            Index put=new Index.Builder(productSkuInfoSearch).index("mall").type("ProductSkuInfo").id(productSkuInfoSearch.getId()+"").build();
            try {
                jestClient.execute(put);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

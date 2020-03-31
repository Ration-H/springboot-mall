# springboot-mall
使用springboot实现网上商城 /n


各个子模块说明：
parent：管理所有依赖的版本
common-utils：通用jar
web-util：Web、Controller层通用jar
service-util：Service层通用jar

遇到的问题及注意事项：
第一天：
1、因为使用的是SpringBoot 2的版本，使用数据库驱动时会报service zone错误，
   在核心配置文件 spring.datasource.url 属性添加 ?serverTimezone=GMT 解决
2、因为使用了tk.mybatis的通用Mapper，MBG理论上不用生成Mapper接口和xml文件 <br/> 
   启动类的@MapperScan要使用tk.mybatis的，我们的Mapper接口要继承tk的Mapper<Class>类   <br/>       

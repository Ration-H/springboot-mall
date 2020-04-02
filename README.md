# springboot-mall
##使用springboot实现网上商城

开发环境

|    依赖    |   版本   |
|:----------:|  :----:  |
|JDK |   8   |  
|Tomcat |   8   |
|Mysql |   5   |
|IDEA |   2018   |

各个模块端口

|     模块   |   端口   |
|:----------:|  :----:  |
|user    |   8090   |  
|manage-web    |   8080   |  
|manage-service|   8070   |
|user-web    |   8081   |  
|user-service|   8071   |


----------
- **generator：逆向工程**
- **mall-parent：管理所有依赖的版本**
- **mall-api：存放pojo类和service接口**    
- **mall-common-utils：通用jar**
- **mall-web-util：Web、Controller层通用jar**
- **mall-service-util：Service层通用jar**

----------
遇到的问题及注意事项：            
Tip 1：    
1. 因为使用的是SpringBoot 2的版本，使用数据库驱动时会报`service zone`错误，   
   在核心配置文件`spring.datasource.url`属性添加`?serverTimezone=GMT`解决  
   因为`mysql8.0`版本要求更加细化      
2. 因为使用了tk.mybatis的通用Mapper，MBG理论上不用生成Mapper接口和xml文件   
   启动类的`@MapperScan`要使用tk.mybatis的，我们的Mapper接口要继承tk的Mapper<Class>类     
3. 使用idea+mvaen+java代码生成文件时，路径要使用`绝对路径`。原因未解决  

----------
Tip 2：
1. 所有`pojo类`要实现`Serializable接口`，因为使用dubbo，所以pojo是在网络上进行传输的  
2. 网络传输`依赖注入`要使用`Dubbo`的@`Reference`，而不是@Autowired
3. `服务层`的`@Service`也要换成`Dubbo`的
4. linux配置各个文件时，要注意路径

----------
Tip 3:
1. 今天启动项目怎么也`连不上ZooKeeper`，报 `ERROR zkclient.ZkClientWrapper: [DUBBO] Timeout! zookeeper server can not be connected in : 30000ms!, dubbo version: 2.0.0, current host: 127.0.0.1`
   原因：因为我一次性启动了多个项目，导致连接时间过长
   解决：**项目一个个启动**  **设置Zookeeper连接超时时间，`spring.dubbo.registry.timeout`**
2. 由于`前后端分离`，后端Controller要加`@CrossOrigin`
3. `增删改`都在一个ServiceImpl的`同一个方法`里处理，非常有意思
   方法位置`com.gdou.mall.manage.service.impl.AttrServiceImpl.saveAttrInfo`
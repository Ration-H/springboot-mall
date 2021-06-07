# springboot-mall
##使用springboot实现网上商城


开发环境

|    依赖    |   版本   |  Window  |   Linux  |
|:----------:|  :----:  |  :----:  |  :----:  |
|JDK |   8   |  √| √  |
|Tomcat |   8   |√| √  |
|Mysql |   5   |√|   |
|IDEA |   2018   |√|   |
|Zookeeper |      | |  √ |
|FastDFS |      | |  √ |
|Nginx |      | |  √ |
|ES |   6   | |  √ |
|kibana |   6   | |  √ |

各个模块端口

|     模块   |   端口   |
|:----------:|  :----:  |
|user    |     8090   |  
|mall-admin|   8888   |
|manage-web  | 8080   |  
|manage-service|     8070   |
|user-web    | 8081   |  
|user-service|       8071   |
|item-web|     8082   |
|search-web|   8083   |
|search-service|     8073   |
|cart-web    | 8084   |
|cart-service|       8074   |
|passport-web| 8085  |
|order-service|      8076   |
|order-web   | 8086  |


----------

各个子模块说明
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
3. 使用`idea+mvaen+java代码逆向工程`生成文件时，路径要使用`绝对路径`。原因未解决  

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
   解决：1. 项目一个个启动  
         2. 设置Zookeeper连接超时时间，`spring.dubbo.registry.timeout`  
2. 由于`前后端分离`，后端Controller要加`@CrossOrigin`  
3. `增删改`都在一个ServiceImpl的`同一个方法`里处理，非常有意思  
   方法位置`com.gdou.mall.manage.service.impl.AttrServiceImpl.saveAttrInfo`  
   
---------
Tip 4:
1. 使用了@JSONField绑定不上数据； 
   原因：SpringBoot默认使用jackSon  
   [解决办法](https://blog.csdn.net/xuqingge/article/details/53561529)   
2. Linux部署FastDFS时，没有storage服务  
   原因：`tracker_server=127.0.0.1:22122`，虽然tracker和storage都在同一台虚拟机上，但是`不能用127.0.0.1`   
   解决：tracker_server=trackerIP:22122  
   
--------
Tip 5:
1. Controller上传图片到FastDFS过程中`连不上Storage服务`  
   原因：Linux`没开放23000端口`  
   解决：iptables`开放23000端口`，ps：`Tracker Server端口`：`22122`  
2. 访问图片时不能显示图片，`<img src="192.168.141.128/group1/M00/00/00/wKiNgF6IhLOAG6y8AATrdYpbQmQ186.png">`  
   原因：src路径`没加http//:`，会被自动加上`http//:localhost:8080/项目名`  
   解决：存储Img src时`加上http//:`  
   
----------
Tip 6:
1. 今天的大坑，id为Long类型，id作为值存入Map<String,Object>,即Long类型转为Object，  
   当从Map出来时，以String类型封装id，此时应该会报`java.lang.ClassCastException: java.lang.Long cannot be cast to java.lang.String`  
   但是，SpringBoot却报`NullPointException`，而且不debug到具体语句，也不会报具体的异常点，导致异常非常难找
2. mybatis in #{}只能修改第一个， 换成${}出现getter错误
   @Param  
     
---------  
Tip 7:
1. 今天测试，发现拦截器只拦截某些Handler，
   原因：SpringBoot启动类不与拦截器类在同一级目录或上级，导致该SpringBoot启动类加载不到该拦截器类  
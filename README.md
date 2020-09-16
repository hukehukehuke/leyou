# leyou
应用架构图

1565533924343
项目演示地址

    商场系统演示地址：http://vip.istio.tech
    后台管理系统：开发中..

项目用到的技术

项目采用前后端分离开发，前端需要独立部署。目前核心的技术栈采用的是SpringBoot2.1.5.RELEASE+Dubbo2.7.2,
前端使用的技术

    nodejs
    axios
    es6
    vue
    sass
    Element UI
    webpack
    vue router
    mockjs

后端使用的技术

后端的主要架构是基于springboot+dubbo+mybatis.

    SpringBoot2.1.6
    Mybatis
    Dubbo2.7.2
    Zookeeper
    Mysql
    Redis
    Elasticsearch
    Kafka
    druid
    Docker
    mybatis generator
    Sentinel

项目模块说明
db_script 本项目的数据库脚本 	使用mysql 	暂时未做分表处理，不过有考虑到分表的情况
gpmall-cashier 收银台，负责支付相关的交互逻辑 	web项目 	8083端口
gpmall-commons 公共的组件 	jar 	公共组件，很多地方都有引用，改动的时候要注意
gpmall-front 咕泡商城的前端项目 	前端项目 	使用vue、node、es等前端技术开发
gpmall-parent 父控文件，用来统一管理所有jar包 	父控文件 	用来统一管理所有项目的jar包的版本
gpmall-shopping 商品/购物车/首页渲染等交互 	web项目 	8081端口
gpmall-user 提供用户相关的交互，如登录、注册、个人中心等 	web项目 	8082端口
market-service 促销活动的Dubbo服务【暂时未联调完成，可以不启动】 	dubbo服务 	20884端口
pay-service 提供支付处理能力 	dubbo服务 	20883端口
shopping-service，提供购物车、推荐商品、商品等服务 	dubbo服务 	20881端口
user-service ，提供用户相关服务 	dubbo服务 	20880端口
order-service ，提供订单服务 	dubbo服务 	20882端口
PRD 		存放prd需求文档，有想参与设计的同学，可以提供prd需求
wiki 		帮助文档，需要每一位同学贡献自己的一份力量
项目开发进度
前台项目整体的规划有

    首页渲染，轮播、自定义展示板块
    商品查询、商品展示、商品详情
    个人中心、用户注册、个人信息修改、收获地址维护
    购物车、订单查询、下单、支付
    促销活动
应用架构图

1565533924343
项目演示地址

    商场系统演示地址：http://vip.istio.tech
    后台管理系统：开发中..

项目用到的技术

项目采用前后端分离开发，前端需要独立部署。目前核心的技术栈采用的是SpringBoot2.1.5.RELEASE+Dubbo2.7.2,
前端使用的技术

    nodejs
    axios
    es6
    vue
    sass
    Element UI
    webpack
    vue router
    mockjs

后端使用的技术

后端的主要架构是基于springboot+dubbo+mybatis.

    SpringBoot2.1.6
    Mybatis
    Dubbo2.7.2
    Zookeeper
    Mysql
    Redis
    Elasticsearch
    Kafka
    druid
    Docker
    mybatis generator
    Sentinel

项目模块说明
db_script 本项目的数据库脚本 	使用mysql 	暂时未做分表处理，不过有考虑到分表的情况
gpmall-cashier 收银台，负责支付相关的交互逻辑 	web项目 	8083端口
gpmall-commons 公共的组件 	jar 	公共组件，很多地方都有引用，改动的时候要注意
gpmall-front 咕泡商城的前端项目 	前端项目 	使用vue、node、es等前端技术开发
gpmall-parent 父控文件，用来统一管理所有jar包 	父控文件 	用来统一管理所有项目的jar包的版本
gpmall-shopping 商品/购物车/首页渲染等交互 	web项目 	8081端口
gpmall-user 提供用户相关的交互，如登录、注册、个人中心等 	web项目 	8082端口
market-service 促销活动的Dubbo服务【暂时未联调完成，可以不启动】 	dubbo服务 	20884端口
pay-service 提供支付处理能力 	dubbo服务 	20883端口
shopping-service，提供购物车、推荐商品、商品等服务 	dubbo服务 	20881端口
user-service ，提供用户相关服务 	dubbo服务 	20880端口
order-service ，提供订单服务 	dubbo服务 	20882端口
PRD 		存放prd需求文档，有想参与设计的同学，可以提供prd需求
wiki 		帮助文档，需要每一位同学贡献自己的一份力量
项目开发进度
前台项目整体的规划有

    首页渲染，轮播、自定义展示板块
    商品查询、商品展示、商品详情
    个人中心、用户注册、个人信息修改、收获地址维护
    购物车、订单查询、下单、支付
    促销活动

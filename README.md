# 健康打卡系统后端
前端项目地址：[健康打卡系统前端](https://github.com/chengsecret/health-check)
## 技术栈
系统以Springcloud为开发框架，redis作缓存处理，MySQL持久化数据，JWT进行权限认证，Zxing生成二维码，七牛云对象存储保存图片。
## 项目说明
系统采用前后端分离的设计架构，前端采用微信小程序，后端基于Java框架开发，前端见：[健康打卡系统前端](https://github.com/chengsecret/health-check)。实现了健康打卡、查看打卡记录、生成校园通行码、提交恢复绿码申请等功能。
## 微服务架构
采用SpringCloud微服务架构，将系统分成了四个部分：注册中心、打卡服务、图片服务、网关。
- eureka服务端：提供服务注册
- 打卡服务：实现打卡相关的功能
- 图片服务：实现用户 提交绿码申请、查看当日通行码 两个功能（都涉及到了图片操作：绿码申请上传核酸证明、通行码是二维码图片）
- 网关：对外统一接口，同时实现token认证与发放的功能
## 运行
执行顺序：eureka-->打卡服务-->图片服务-->网关。顺序不能错

需要:
- 修改打卡服务与图片服务的application-dev.yml文件中的数据库与redis配置
- 前端是微信小程序,需要去微信小程序官网获取appid与appSecret,配置到打卡服务中的application-dev.yml文件中（用于登录 wx.login ）
- 图片服务采用了七牛云对象存储,需要去七牛云官网开辟存储空间,将accessKey等信息配置到图片服务的application-dev.yml文件中
- 使用百度地图的api逆向解析地址为经纬度,需要去百度地图创建账号,获取一个ak,放到Service-Daka\src\main\java\xyz\ctstudy\utils\RiskAreaUtil.java中的ak变量中






# KyApi在线开放平台


## 技术栈

- Spring Boot 2.7.0（包含Starter开发）
- Spring MVC
- SpringCloud-GateWay
- Nacos 2.1.2（服务注册中心）
- Dubbo 3.0.9（RPC 远程调用服务）
- MySQL
- MyBatis
- MyBatis Plus
- Redis
- Spring AOP
- Swagger + Knife4j 接口文档

访问 localhost:7529/api/doc.html 就能在线调试接口了，不需要前端配合

启动模块顺序： kyapi-backend => kyapi-interface => kyapi-gateway，否则可能发生报错

前端（npm install dev）要在后端模块 kyapi-backend启动后再启动

前端访问管理员账户和密码=>   账号：kyle    密码：12345678

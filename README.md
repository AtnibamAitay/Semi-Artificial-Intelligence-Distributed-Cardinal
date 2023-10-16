# Semi-Artificial-Intelligence-Distributed-Cardinal

## 1.模块
```
SAIDC
├── saidc-api                                   // 接口模块
│       └── api-system                          // 系统接口
│       └── api-user                            // 用户接口
├── saidc-auth                                  // 认证中心
├── saidc-common                                // 通用模块
│       └── common-core                         // 核心模块
│       └── common-datascope                    // 权限范围（待建立模块）
│       └── common-datasource                   // 多数据源
│       └── common-log                          // 日志记录
│       └── common-minio                        // 对象存储
│       └── common-redis                        // 缓存服务
│       └── common-seata                        // 分布式事务（待建立模块）
│       └── common-security                     // 安全模块
│       └── common-service-aop                  // AOP服务模块
│       └── common-swagger                      // API文档
├── saidc-gateway                               // 网关模块 [6500]
├── saidc-modules                               // 业务模块
│       └── saidc-system                        // 系统模块 [6505]
│       └── saidc-user                          // 系统模块 [6506]
├──pom.xml                                      // 公共依赖
```
# blog-springcloud

Spring Cloud 博客系统微服务练习项目。

| 模块 | 说明 | 端口 |
| --- | --- | --- |
| `blog-common` | 公共 POJO / 工具（JWTUtils、SecurityUtil、Result） | — |
| `blog-info-api` | 博客服务契约（Feign 接口定义） | — |
| `blog-info-service` | 博客服务 | 9090 |
| `user-info-api` | 用户服务契约 | — |
| `user-info-service` | 用户服务 | 9091 |
| `gateway-service` | Spring Cloud Gateway + 统一鉴权 AuthFilter | 10030 |

技术栈：Spring Boot 4.1.2 / Spring Cloud 2025.1.4 / Spring Cloud Alibaba 2025.1.0.0（Nacos）/
MyBatis-Plus / MySQL / Redis / RabbitMQ。

## 一、先配置环境变量（必读）

各 `application.yml` 里**不再硬编码任何密码**，全部写成 `${XXX}` 占位符，
启动前必须提供对应的环境变量，否则服务启动会报 `Could not resolve placeholder`。

| 环境变量 | 使用方 | 说明 |
| --- | --- | --- |
| `DB_USERNAME` | blog / user | MySQL 用户名，默认 `root` |
| `DB_PASSWORD` | blog / user | MySQL 密码 |
| `NACOS_USERNAME` | blog / user / gateway | Nacos 用户名，默认 `nacos` |
| `NACOS_PASSWORD` | blog / user / gateway | Nacos 密码 |
| `RABBITMQ_USERNAME` | user | RabbitMQ 用户名，默认 `guest` |
| `RABBITMQ_PASSWORD` | user | RabbitMQ 密码 |
| `MAIL_USERNAME` | user | 163 邮箱账号 |
| `MAIL_PASSWORD` | user | 163 邮箱授权码（不是登录密码） |
| `JWT_SECRET` | blog / user / gateway | JWT 签名密钥，Base64，解码后 ≥ 32 字节 |

`JWT_SECRET` 特别注意：网关 `jwt.secret` 与 `blog-common` 的 `JWTUtils` **必须用同一个值**，
否则网关永远验不过用户登录签发的 token。生成新密钥：`openssl rand -base64 48`
（换密钥会让此前所有 token 失效，用户需重新登录）。

### 本地怎么设置

```powershell
# 1) 生成自己的本地配置文件（该文件已被 .gitignore 忽略，不会提交）
Copy-Item scripts\env.local.example.ps1 scripts\env.local.ps1
# 2) 编辑 scripts\env.local.ps1，把 <尖括号> 换成真实值
# 3) scripts\start-local.ps1 会自动加载它；手动跑的话先 . .\scripts\env.local.ps1
```

用 VS Code 的 `launch.json` 启动时，环境变量要来自系统环境（`setx`）或用户级环境变量，
因为 Java 调试配置不会读 `scripts\env.local.ps1`。

## 二、启动

前置：Nacos 3.x（控制台 8080 / HTTP 8848 / gRPC 9848）、MySQL、Redis、RabbitMQ 已就绪。

```powershell
mvn -o install -DskipTests        # 先装 blog-common / *-api 到本地仓库
.\scripts\start-local.ps1         # 按 yml 里的端口依次启动三个服务
.\scripts\start-local.ps1 -Stop   # 停止
```

访问入口统一走网关 `http://127.0.0.1:10030`：`/user/**` → user-service，`/blog/**` → blog-service。

## 三、数据库

`mysql.sql` / `mysql-schema.sql` 是建库脚本，里面的 `123456` 等是**演示用的业务数据**
（示例用户的密码，入库已是加盐 MD5），不是任何基础设施账号。

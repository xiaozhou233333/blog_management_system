# =====================================================================
#  本地敏感配置模板
#
#  用法：
#    1) 复制一份改名（注意去掉 .example）：
#         Copy-Item scripts\env.local.example.ps1 scripts\env.local.ps1
#    2) 把下面的 <尖括号> 换成你自己的真实值
#    3) scripts\start-local.ps1 会自动加载 scripts\env.local.ps1
#       （手动启动时可以先在终端里 . .\scripts\env.local.ps1）
#
#  scripts\env.local.ps1 已被 .gitignore 忽略，不会提交到仓库；
#  这个 .example 文件里的值全是占位符，可以安全提交。
#
#  对应的 application.yml 占位符：
#    ${DB_USERNAME:root}  ${DB_PASSWORD}
#    ${NACOS_USERNAME:nacos}  ${NACOS_PASSWORD}
#    ${RABBITMQ_USERNAME:guest}  ${RABBITMQ_PASSWORD}
#    ${MAIL_USERNAME}  ${MAIL_PASSWORD}
#    ${JWT_SECRET}   <- 网关 jwt.secret 与 blog-common JWTUtils 共用这一个
# =====================================================================

# ---- MySQL ----
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "<YOUR_DB_PASSWORD>"

# ---- Nacos（user/blog/gateway 三个服务都要）----
$env:NACOS_USERNAME = "nacos"
$env:NACOS_PASSWORD = "<YOUR_NACOS_PASSWORD>"

# ---- RabbitMQ（user-service 用）----
$env:RABBITMQ_USERNAME = "guest"
$env:RABBITMQ_PASSWORD = "<YOUR_RABBITMQ_PASSWORD>"

# ---- 邮件（user-service 发验证码用，163 填授权码而不是登录密码）----
$env:MAIL_USERNAME = "<YOUR_MAIL_ACCOUNT>"
$env:MAIL_PASSWORD = "<YOUR_MAIL_AUTH_CODE>"

# ---- JWT 签名密钥：Base64，解码后至少 32 字节 ----
# 生成一个新值：  openssl rand -base64 48
# 注意：网关 jwt.secret 和 blog-common 的 JWTUtils 必须用同一个值，
#       换密钥后此前发出的所有 token 都会失效（用户需要重新登录）。
$env:JWT_SECRET = "<YOUR_BASE64_JWT_SECRET_32_BYTES_OR_MORE>"

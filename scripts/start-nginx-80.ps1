# 释放 80 端口给 nginx 使用（需要管理员权限）
# 用法：右键 PowerShell -> 以管理员身份运行，然后执行
#   powershell -ExecutionPolicy Bypass -File "D:\Program Files\Java Files 2\Project\blog-springcloud\scripts\start-nginx-80.ps1"

$ErrorActionPreference = 'Stop'

$identity = [Security.Principal.WindowsIdentity]::GetCurrent()
$principal = New-Object Security.Principal.WindowsPrincipal($identity)
if (-not $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Write-Host "请以管理员身份运行此脚本。" -ForegroundColor Red
    exit 1
}

$nginxHome = 'D:\Program Files\Java Files 2\Project\blog-springcloud\resources'
$nginxExe  = Join-Path $nginxHome 'nginx.exe'

# 1. 停止并禁用占用 80 端口的 Apache 服务
$svc = Get-Service -Name 'Apache24' -ErrorAction SilentlyContinue
if ($svc) {
    if ($svc.Status -ne 'Stopped') {
        Write-Host "停止服务 Apache24 ..." -ForegroundColor Yellow
        Stop-Service -Name 'Apache24' -Force
    }
    Set-Service -Name 'Apache24' -StartupType Manual   # 想彻底不启动可改成 Disabled
    Write-Host "Apache24 已停止，启动类型改为 Manual。" -ForegroundColor Green
}

# 2. 确认 80 端口已空出
$busy = Get-NetTCPConnection -LocalPort 80 -State Listen -ErrorAction SilentlyContinue
if ($busy) {
    $pid80 = ($busy | Select-Object -First 1).OwningProcess
    $p = Get-Process -Id $pid80 -ErrorAction SilentlyContinue
    Write-Host "80 端口仍被占用：PID=$pid80 进程=$($p.ProcessName)" -ForegroundColor Red
    exit 1
}

# 3. 测试配置并启动 nginx
Push-Location $nginxHome
try {
    & $nginxExe -t
    if ($LASTEXITCODE -ne 0) { Write-Host "nginx 配置检查失败。" -ForegroundColor Red; exit 1 }
    & $nginxExe
} finally {
    Pop-Location
}

Start-Sleep -Seconds 2
$listen = Get-NetTCPConnection -LocalPort 80 -State Listen -ErrorAction SilentlyContinue
if ($listen) {
    $pidNginx = ($listen | Select-Object -First 1).OwningProcess
    Write-Host "nginx 已在 80 端口监听，PID=$pidNginx" -ForegroundColor Green
    Write-Host "浏览器访问 http://localhost/ 验证。" -ForegroundColor Green
} else {
    Write-Host "nginx 未能监听 80，请查看 $nginxHome\logs\error.log" -ForegroundColor Red
}

<#
  本地一键启动 / 停止（Windows PowerShell）

  用法：
    .\scripts\start-local.ps1              # Nacos 注册发现模式（读 application.yml 里的账号密码）
    .\scripts\start-local.ps1 -Local       # 离线模式：不连 Nacos，服务间直连（application-local.yml）
    .\scripts\start-local.ps1 -Stop        # 停止全部

  说明：
    - 端口不写死，自动从各模块的 application.yml 里读 server.port，
      所以你在 yml 里改端口，这个脚本不用跟着改。
    - 前置：mvn -o install -DskipTests
#>
param(
    [switch]$Stop,
    [switch]$Local
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$logDir = Join-Path $root "logs"
New-Item -ItemType Directory -Force -Path $logDir | Out-Null

# 优先用 JAVA_HOME，找不到就退回 PATH 里的 java
$java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME "bin\java.exe" } else { "java" }
if ($env:JAVA_HOME -and !(Test-Path $java)) { $java = "java" }

$apps = @(
    @{ name = "user-service";    jar = "$root\user-info-service\target\user-info-service-1.0-SNAPSHOT.jar"; yml = "$root\user-info-service\src\main\resources\application.yml" },
    @{ name = "blog-service";    jar = "$root\blog-info-service\target\blog-info-service-1.0-SNAPSHOT.jar"; yml = "$root\blog-info-service\src\main\resources\application.yml" },
    @{ name = "gateway-service"; jar = "$root\gateway-service\target\gateway-service-1.0-SNAPSHOT.jar";     yml = "$root\gateway-service\src\main\resources\application.yml" }
)

# 从 yml 读端口，读不到就报错（不要瞎猜）
foreach ($a in $apps) {
    $port = $null
    if (Test-Path $a.yml) {
        $m = Select-String -Path $a.yml -Pattern '^\s{2}port:\s*(\d+)' | Select-Object -First 1
        if ($m) { $port = [int]$m.Matches[0].Groups[1].Value }
    }
    if (!$port) {
        Write-Host ("无法从 {0} 读取 server.port" -f $a.yml) -ForegroundColor Red
        return
    }
    $a.port = $port
}

function Stop-Apps {
    Get-CimInstance Win32_Process -Filter "Name='java.exe'" |
        Where-Object {
            $cl = $_.CommandLine
            if (!$cl) { return $false }
            $cl.Contains("blog-springcloud") -or
            $cl.Contains("com.example.UserServiceApplication") -or
            $cl.Contains("com.example.BlogServiceApplication") -or
            $cl.Contains("com.example.GatewayApplication")
        } |
        ForEach-Object {
            Write-Host ("  stop pid=" + $_.ProcessId)
            Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue
        }
}

if ($Stop) {
    Write-Host "停止所有服务..."
    Stop-Apps
    Write-Host "已停止。"
    return
}

foreach ($a in $apps) {
    if (!(Test-Path $a.jar)) {
        Write-Host "缺少 $($a.jar)`n请先执行： mvn -o install -DskipTests" -ForegroundColor Red
        return
    }
}

Write-Host "停止可能残留的旧进程..."
Stop-Apps
Start-Sleep -Seconds 2

$extraArgs = if ($Local) { @("--spring.profiles.active=local") } else { @() }

foreach ($a in $apps) {
    $argsList = @("-Dfile.encoding=UTF-8", "-jar", "`"$($a.jar)`"") + $extraArgs
    $p = Start-Process -FilePath $java -ArgumentList $argsList `
        -RedirectStandardOutput "$logDir\$($a.name).out.log" `
        -RedirectStandardError  "$logDir\$($a.name).err.log" `
        -WindowStyle Hidden -PassThru
    Write-Host ("  start " + $a.name + " pid=" + $p.Id + " port=" + $a.port)
}

Write-Host "`n等待端口就绪..."
$deadline = (Get-Date).AddSeconds(90)
do {
    Start-Sleep -Seconds 3
    $up = @($apps | Where-Object { Get-NetTCPConnection -State Listen -LocalPort $_.port -ErrorAction SilentlyContinue })
} until ($up.Count -eq $apps.Count -or (Get-Date) -gt $deadline)

Write-Host ""
$allUp = $true
foreach ($a in $apps) {
    $ok = [bool](Get-NetTCPConnection -State Listen -LocalPort $a.port -ErrorAction SilentlyContinue)
    $mark = if ($ok) { "UP  " } else { "DOWN"; $allUp = $false }
    Write-Host ("  [{0}] {1,-16} http://127.0.0.1:{2}" -f $mark, $a.name, $a.port)
    if (!$ok) { Write-Host ("          -> 看日志: " + $logDir + "\" + $a.name + ".out.log") -ForegroundColor Yellow }
}

Write-Host ""
Write-Host ("模式：" + $(if ($Local) { "local（无注册中心，直连）" } else { "Nacos 注册发现" }))
Write-Host ("日志：" + $logDir)
Write-Host "停止：.\scripts\start-local.ps1 -Stop"
if (!$allUp) { exit 1 }

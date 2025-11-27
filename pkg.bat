@echo off
chcp 65001 > nul
set BASE_DIR=%~dp0
REM added double quotation marks to avoid the issue caused by the folder names containing spaces.
REM removed the last 5 chars(which means \bin\) to get the base DIR.
REM set BASE_DIR="%BASE_DIR:~0,-5%"

REM 打包目录
set PKG_DIR="pkg"

REM 工程模块目录
set PROJECT_DIR=boot-module-biz
set MODULE_RES_DIR=src\main\resources
set MODULE_TARGET_DIR=target

REM 工程模块名称
set PROJECT_NAME=smart-boot-biz-zhongtielanzhou

REM 修改maven命令路径
set MVN_CMD=mvn
set MVN_OPT=clean install

REM 执行maven编译
set MVN_CMD=%MVN_CMD% %MVN_OPT%
call %MVN_CMD%

if not exist "%PROJECT_DIR%\%MODULE_TARGET_DIR%\%PROJECT_NAME%.jar" echo "%PROJECT_DIR%\%MODULE_TARGET_DIR%\%PROJECT_NAME%.jar不存在"

REM 创建打包文件夹
if exist %PKG_DIR% (
	echo "%PKG_DIR% 已经存在, 删除文件夹"
    rd /s /q %PKG_DIR%
)

md %PKG_DIR%
md "%PKG_DIR%\%PROJECT_NAME%"
md "%PKG_DIR%\%PROJECT_NAME%\config"

REM 复制打包文件
copy "%PROJECT_DIR%\%MODULE_TARGET_DIR%\%PROJECT_NAME%.jar" %PKG_DIR%\%PROJECT_NAME%

REM 打包yml配置文件
for /r %%i in ("%PROJECT_DIR%\%MODULE_RES_DIR%\*.yml") do (
  copy %%i %PKG_DIR%\%PROJECT_NAME%\config
)


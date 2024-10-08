# 快速开始

本文会帮助你从头部署 **Y20** 系统。

## 1. 准备环境
预先安装以下程序：
- java 11
- openresty
- consul
- redis
- rabbitmq
- minio
- mysql
- mongodb
- influxdb

## 2. 下载程序
前往 `https://github.com/airxiechao/Y20/releases`，下载最新程序包 `y20-on-premise.zip` 并解压

## 3. 后端启动
后端主要配置文件是 `y20-config-on-premise/common.yml`，至少修改如下配置：
- server.url 网站访问地址
- consul 配置
- redis 配置
- rabbitmq 配置
- minio 配置
- mysql 配置
- mongodb 配置
- influxdb 配置

进入 `y20-backend-on-premise/allinone-on-premise`，启动后端 `java -jar boot-allinone-on-premise.jar`

## 4. 前端启动
前端的配置文件 `y20-gateway-on-premise/conf/init.conf`，修改如下配置：
- static_dir 指向 `y20-frontend-on-premise`目录的绝对路径

配置文件 `y20-gateway-on-premise/conf/server.common.conf` 中修改 dns 指向本地的 consul dns 服务

将 Openresty 的 `nginx/conf` 链接到 `y20-gateway-on-premise/conf`，然后重新加载 Openresty `openresty -s reload`


安装完成！



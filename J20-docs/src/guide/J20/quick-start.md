# 快速开始

本文会帮助你从头部署 **J20** 系统。

## 1. 准备环境
服务器预先安装以下程序：
- **jdk**: 11
- **openresty**
- **mysql**: 8
- **flink**: 1.16.3
- **kafka**: 3.7.0
- **minio**
- **elasticsearch**: 8.13.4

捕获流量的机器上安装：
- **pcap**
  - linux: libpcap
  - windows: WinPcap or Npcap in WinPcap API-compatible Mode

## 2. 下载程序
前往 `https://github.com/airxiechao/J20/releases`，下载最新程序：
- 后端服务：`j20-backend.zip`
- 前端程序：`j20-frontend.zip`
- Flink任务：`j20-detection-job-boot.jar`
- 流量探针：`j20-probe-network.zip`

## 3. 后端启动
首先将 `j20-detection-job-boot.jar` 上传到 Flink。
解压 `j20-backend.zip`，后端的配置文件参考 `J20-config/application.yml`，修改其中的配置：
- spring.datasource 数据库配置
- flink 配置
- kafka 配置
- minio 配置
- elasticsearch 配置

启动后端 `java -jar j20-allinone-boot.jar`

## 4. 前端启动
解压 `j20-frontend.zip`，在 Openresty 的 `nginx/conf` 中配置前端路径和后端代理：
```
server {
  listen 80;

  # 前端分发
  root /<前端路径>/;
  index index.html index.htm;
  try_files $uri $uri/ /index.html;

  # 后端代理
  location ~ /api/(.*) {
    set $path $1;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_pass http://127.0.0.1:8101/api/$path?$args;
  }
}
```
重新加载 Openresty `openresty -s reload`

## 6. 流量探针启动
将 `j20-probe-network.zip` 复制并解压到捕获流量的机器上。流量探针的配置文件参考 `J20-config/probe-network.yml`，修改其中的配置：
- capture 捕获配置
  - ip 捕获设备IP
  - protocols 捕获协议，支持 TCP/UDP/HTTP
  - output 输出配置

启动流量探针 `java -jar j20-probe-network-boot.jar`

安装完成！



# 产品介绍
鲲擎-J20 是一个网络安全检测与态势感知系统。它的功能是通过对流量进行采集和检测，识别流量中的网络安全事件，比如攻击入侵事件、行为违规事件等。

## 基本概念

### 1. 流量探针
流量探针是运行在采集目标机器上的一个抓包程序，该程序通过 **pcap** 捕获指定设备的网络包，并进行协议解析。

### 2. 日志协议
流量解析支持的协议有：
- TCP
  - srcIp 源IP
  - srcPort 源端口
  - dstIp 目的IP
  - dstPort 目的端口
  - syn SYN 标志
  - ack ACK 标志
  - fin FIN 标志
  - psh PSH 标志
  - rst RST 标志
  - urg URG 标志
  - sequenceNumber 序列号
  - acknowledgmentNumber 确认号
  - length 长度
- UDP
  - srcIp 源IP
  - srcPort 源端口
  - dstIp 目的IP
  - dstPort 目的端口
  - length 长度
- HTTP
  - uri URI
  - host 域名
  - path 请求路径
  - query 请求参数
  - method 请求方法
  - reqContentType 请求内容类型
  - reqJson 请求JSON
  - respContentType 响应内容类型
  - respJson 响应JSON
  - respStatus 响应状态码
  - realSrcIp 真实源IP
  - srcIp 源IP
  - srcPort 源端口
  - dstIp 目的IP
  - dstPort 目的端口

解析后的数据叫做 **日志**，将被发送到指定的 Kafka 的主题中。

### 3. 日志数据源
原始日志被发送到的 Kafka 主题被称作 **日志数据源**。

### 4. 事件类型
事件是日志经过检测的结果。事件类型可以分为：
- 记录
  - 请求记录
  - 连接记录
- 告警
  - 攻击入侵
  - 违规访问

### 5. 策略规则
事件的检测方法叫 **规则**。规则包含以下几个部分：
- 协议：指定规则检测的协议类型
- 过滤条件：根据日志内容进行筛选的条件
- 统计条件：根据时间窗口进行统计的条件
- 输出：指定输出的事件

### 6. 检测任务
**任务** 是运行一个或多个规则的运行实例。它将从指定的 **日志数据源** 读取日志，然后根据 **规则** 进行检测。检测到的事件在事件列表中进行查看。

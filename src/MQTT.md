# MQTT协议
## 1.概述
    MQTT是一个客户端服务端架构的发布/订阅模式的消息传输协议。它的设计思想是轻巧、开放、简单、规范，易于实现。这些特点使得它对很多场景来说都是很好的选择，
    特别是对于受限的环境如机器与机器的通信（M2M）以及物联网环境（IoT）。
## 2.服务质量等级和协议流程 QoS
    
**QoS 0:最多分发一次**
    
    接收者不会发送响应，发送者也不会重试。消息可能送达一次也可能根本没送达。
**QoS 1: 至少分发一次**

    服务质量确保消息至少送达一次!重复

**QoS 2: 仅分发一次**

    最高等级的服务质量，消息丢失和重复都是不可接受的。使用这个服务质量等级会有额外的开销。
    
## 3.主题名和主题过滤器（Topic Names and Topic Filters）
### 3.1主题层级分隔符 Topic level separator

    斜杠（‘/’）用于分割主题的每个层级

### 3.2多层通配符 Multi-level wildcard

    数字标志（‘#’ ）是用于匹配主题中任意层级的通配符
    
`eg:`
+ “sport/tennis/player1”
+ “sport/tennis/player1/ranking”
+ “sport/tennis/player1/score/wimbledon”

**解释**

+ “sport/#”也匹配单独的 “sport” ，因为 # 包括它的父级。有效
+ “#”是有效的，会收到所有的应用消息。
+ “sport/tennis/#”也是有效的。
+ “sport/tennis#”是无效的。
+ “sport/tennis/#/ranking”是无效的。
### 3.2单层通配符
    加号 (‘+’) 是只能用于单个主题层级匹配的通配符
    
eg:
    
    例如， “sport/tennis/+” 匹配 “sport/tennis/player1” 和 “sport/tennis/player2” ，但是不匹配 “sport/tennis/player1/ranking” 。
    同时，由于单层通配符只能匹配一个层级， “sport/+” 不匹配 “sport” 但是却匹配 “sport/”。
**解释**
+ “+” 是有效的。
+ “+/tennis/#” 是有效的。
+ “sport+” 是无效的。
+ “sport/+/player1” 也是有效的。
+ “/finance” 匹配 “+/+” 和 “/+” ，但是不匹配 “+”。

## 4.安全
    推荐提供TLS的服务端实现应该使用TCP端口8883
    
    MQTT方案通常部署在不安全的通信环境中。在这种情况下，协议实现通常需要提供这些机制：
    用户和设备身份认证
    服务端资源访问授权
    MQTT控制报文和内嵌应用数据的完整性校验
    MQTT控制报文和内嵌应用数据的隐私控制



    
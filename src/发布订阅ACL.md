# 发布订阅 ACL
    发布订阅 ACL 指对 发布 (PUBLISH)/订阅 (SUBSCRIBE) 操作的 权限控制。
    例如拒绝用户名为 Anna 向 open/elsa/door 发布消息。
    EMQ X 支持通过客户端发布订阅 ACL 进行客户端权限的管理

**EMQ X 支持使用配置文件、外部主流数据库和自定义 HTTP API 作为 ACL 数据源。**
    
    客户端订阅主题、发布消息时插件通过检查目标主题（Topic）是否在指定数据源允许/禁止列表内来实现对客户端的发布、订阅权限管理。
##  一.定义ACL
    内置 ACL 是优先级最低规则表，在所有的 ACL 检查完成后，如果仍然未命中则检查默认的 ACL 规则。
    配置文件提供认证数据源    
    
    %% 允许 "dashboard" 用户 订阅 "$SYS/#" 主题
    {allow, {user, "dashboard"}, subscribe, ["$SYS/#"]}.
    
    %% 允许 IP 地址为 "127.0.0.1" 的用户 发布/订阅 "#SYS/#"，"#" 主题
    {allow, {ipaddr, "127.0.0.1"}, pubsub, ["$SYS/#", "#"]}.
    
    %% 拒绝 "所有用户" 订阅 "$SYS/#" "#" 主题
    {deny, all, subscribe, ["$SYS/#", {eq, "#"}]}.
    
    %% 允许其它任意的发布订阅操作
    {allow, all}.
## 二.外部数据库eg：MySQL ACL
    ACL 规则表
    CREATE TABLE `mqtt_acl` (
      `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
      `allow` int(1) DEFAULT 1 COMMENT '0: deny, 1: allow',
      `ipaddr` varchar(60) DEFAULT NULL COMMENT 'IpAddress',
      `username` varchar(100) DEFAULT NULL COMMENT 'Username',
      `clientid` varchar(100) DEFAULT NULL COMMENT 'ClientId',
      `access` int(2) NOT NULL COMMENT '1: subscribe, 2: publish, 3: pubsub',
      `topic` varchar(100) NOT NULL DEFAULT '' COMMENT 'Topic Filter',
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
    规则表字段说明：
    
    allow：禁止（0），允许（1）
    ipaddr：设置 IP 地址
    username：连接客户端的用户名，此处的值如果设置为 $all 表示该规则适用于所有的用户
    clientid：连接客户端的 Client ID
    access：允许的操作：订阅（1），发布（2），订阅发布都可以（3）
    topic：控制的主题，可以使用通配符，并且可以在主题中加入占位符来匹配客户端信息，例如 t/%c 则在匹配时主题将会替换为当前客户端的 Client ID

    
    -- 所有用户不可以订阅系统主题
    INSERT INTO mqtt_acl (allow, ipaddr, username, clientid, access, topic) VALUES (0, NULL, '$all', NULL, 1, '$SYS/#');
    
    -- 允许 10.59.1.100 上的客户端订阅系统主题
    INSERT INTO mqtt_acl (allow, ipaddr, username, clientid, access, topic) VALUES (1, '10.59.1.100', NULL, NULL, 1, '$SYS/#');
    
    -- 禁止客户端订阅 /smarthome/+/temperature 主题
    INSERT INTO mqtt_acl (allow, ipaddr, username, clientid, access, topic) VALUES (0, NULL, NULL, NULL, 1, '/smarthome/+/temperature');
    
    -- 允许客户端订阅包含自身 Client ID 的 /smarthome/${clientid}/temperature 主题
    INSERT INTO mqtt_acl (allow, ipaddr, username, clientid, access, topic) VALUES (1, NULL, NULL, NULL, 1, '/smarthome/%c/temperature');
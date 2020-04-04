#  使用nginx为emq集群配置负载均衡
## 1.安装nginx
### 1).安装nginx的依赖包
    # 解决依赖包openssl安装
    sudo apt-get install openssl libssl-dev
    
    # 解决依赖包pcre安装
    sudo apt-get install libpcre3 libpcre3-dev
    
    # 解决依赖包zlib安装
    sudo apt-get install zlib1g-de
### 2）.下载nginx

    # 下载nginx(到官网查看对应版本)
    wget http://nginx.org/download/nginx-1.13.1.tar.gz
    # 解压文件到/opt目录下
    tar -xzvf nginx-1.13.1.tar.gz -C /opt
### 3).安装nginx
    # 到安装目录下，编译过程中开启 --with-stream,tcp转发必须模块，因为emqx转发的是tcp连接
    ./configure --with-http_stub_status_module --with-http_ssl_module --with-stream --prefix=/opt/nginx
    make && make install

## 2.编辑nginx配置文件，用nginx 18084端口代理emq1883端口
    
    
    vim /opt/nginx/conf/emqx.conf
     
    stream{
        upstream emqx_cluster {
            server 192.168.3.233:1883;
            server 192.168.3.234:1883;
            server 192.168.3.235:1883;
        }
     
        server{
           listen  18084 so_keepalive=on;
           proxy_connect_timeout 10s;
           proxy_timeout 20s;
           proxy_pass emqx_cluster;
        }
    }

**检查配置文件是否ok**

    ./sbin/nginx -t
     
    nginx: the configuration file /opt/software/nginx/conf/nginx.conf syntax is ok
    nginx: configuration file /opt/software/nginx/conf/nginx.conf test is successful
     
    #出现上面两句说明配置ok，可以正常启动

## 3.启动nginx
    ./sbin/nginx
    
    # 查看是否启动成功
    [root@iZwz97pma26sz5rguhiof1Z nginx]# ps -ef | grep nginx
    root     19440     1  0 10:42 ?        00:00:00 nginx: master process ./sbin/nginx
    nobody   19441 19440  0 10:42 ?        00:00:00 nginx: worker process
    nobody   19442 19440  0 10:42 ?        00:00:00 nginx: worker process
    nobody   19443 19440  0 10:42 ?        00:00:00 nginx: worker process
    root     19452 10808  0 10:42 pts/1    00:00:00 grep --color=auto nginx

## 4.nginx配置负载均衡的几种策略
### 1）、轮询（默认）
    每个请求按时间顺序逐一分配到不同的后端服务器，如果后端服务器down掉，能自动剔除。 
    
    upstream backserver { 
        server 192.168.0.14; 
        server 192.168.0.15; 
    } 
### 2）、指定权重
    指定轮询几率，weight和访问比率成正比，用于后端服务器性能不均的情况。 
    
    upstream backserver { 
        server 192.168.0.14 weight=8; 
        server 192.168.0.15 weight=10; 
    } 
### 3）、IP绑定 ip_hash
    每个请求按访问ip的hash结果分配，这样每个访客固定访问一个后端服务器，可以解决session的问题。 
    
    upstream backserver { 
        ip_hash; 
        server 192.168.0.14:88; 
        server 192.168.0.15:80; 
    } 
### 4）、fair（第三方）
    按后端服务器的响应时间来分配请求，响应时间短的优先分配。 
    
    upstream backserver { 
        server server1; 
        server server2; 
        fair; 
    } 
    
### 5）、url_hash（第三方）
    按访问url的hash结果来分配请求，使每个url定向到同一个后端服务器，后端服务器为缓存时比较有效。
     
    upstream backserver { 
        server squid1:3128; 
        server squid2:3128; 
        hash $request_uri; 
        hash_method crc32; 
    } 
    
### 6.可在server中增加特定参数
    server 127.0.0.1:9090 down; (down 表示当前的server暂时不参与负载) 
    server 127.0.0.1:8080 weight=2; (weight 默认为1.weight越大，负载的权重就越大) 
    server 127.0.0.1:7070 backup; (其它所有的非backup机器down或者忙的时候，请求backup机器) 
    
    max_fails ：允许请求失败的次数默认为1.当超过最大次数时，返回proxy_next_upstream 模块定义的错误 
    fail_timeout:max_fails次失败后，暂停的时间
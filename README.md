# liaoin-security-pc
了赢科技权限验证PC端封装

### 项目结构

--`liaoin-security`  父级目录  
----`liaoin-security-app`  APP封装  
----`liaoin-security-core`  具体代码封装  
----`liaoin-security-pc`  浏览器封装  
----`liaoin-security-test`  测试  


### 使用说明
- 具体配置说明，这里的格式是yml
~~~
liaoin:
  security: 
#浏览器环境配置项 BrowserProperties
    browser:
      #默认注册页面
      signUpUrl: /demo-signUp.html
      #默认登录页面
      loginPage: /demo-signIn.html
      #默认退出页面
      signOutUrl:
      #登陆返回方式
      loginType: REDIRECT
      #记住我的过期时间（单位:秒）
      rememberMeSeconds:
      session:
        #同一个用户在系统中的最大session数，默认1
        maximumSessions: 1
        #达到最大session时是否阻止新的登录请求，默认为false，不阻止，新的登录会将老的登录失效掉
        maxSessionsPreventsLogin: false
        #session失效时跳转的地址
        sessionInvalidUrl: /session/invalid
#图片验证码配置项 ValidateCodeProperties
    code:
      #图片验证码属性配置
      image:
        #宽度
        length: 6
        #高度
        width: 100

        #短信验证码属性配置
        #那些拦截需要验证码
        url: /user/*
        #过期时间
        expireIn:
        #长度
        length:
#三方登陆相关配置项 SocialProperties
    social:
      #过滤链需要过滤器验证的url地址
      filterProcessesUrl: /auth
      #QQ登陆配置属性
      qq:
        #服务商标识
        providerId: qq
        app-id:
        app-secret:
      #微信登陆配置属性
      weixin:
        #第三方id，用来决定发起第三方登录的url，默认是 weixin。
        providerId: weixin
        app-id:
        app-secret:
#认证服务器注册的第三方应用配置项  OAuth2Properties
    oauth2:
      #使用jwt时为token签名的密匙
      jwtSigningKey:
      #具体应用的密钥和密码，这里是数组
      clients[0]:
        clientId:
        clientIdSecret:
        accessTokenValiditySeconds:
      clients[1]:
        clientId:
        clientIdSecret:
        accessTokenValiditySeconds:
#集群会话管理
spring:
  session:
    store-type: none


~~~
- 数据库中需要建表的sql语句
~~~
 -- 登陆记住我功能的表
 CREATE TABLE persistent_lgins(username VARCHAR(64) NOT NULL,series VARCHAR(64) PRIMARY KEY,token VARCHAR(64) NOT NULL,lat_used TIMESTAMP NOT NULL);
 -- 社交登陆用的表
 CREATE TABLE UserConnection (
   userId VARCHAR (255) NOT NULL,
   providerId VARCHAR (255) NOT NULL,
   providerUserId VARCHAR (255),
   rank INT NOT NULL,
   displayName VARCHAR (255),
   profileUrl VARCHAR (512),
   imageUrl VARCHAR (512),
   accessToken VARCHAR (512) NOT NULL,
   secret VARCHAR (512),
   refreshToken VARCHAR (512),
   expireTime BIGINT,
   PRIMARY KEY (
     userId,
     providerId,
     providerUserId
   )
 ) ;
 
 CREATE UNIQUE INDEX UserConnectionRank ON UserConnection(userId,providerId,rank);
~~~
- 扩展点说明
~~~
1. 密码加密解密策略
    org.springframework.security.crypto.password.PasswordEncoder
2. 表单登陆用户信息读取逻辑
    org.springframework.security.core.userdetails.UserDetailsService
3. 第三登陆用户信息读取逻辑
    org.springframework.social.security.SocialUserDetailsService
4. session失效时的处理策略
    org.springframework.security.web.session.InvalidSessionStrategy
    com.liaoin.security.pc.session.LiaoinInvalidSessionStrategy
5. 并发登陆导致前一个session失效时的处理策略
    org.springframework.security.web.session.SessionInformationExpiredEvent
6. 退出时的处理策略
    org.springframework.security.web.authentication.logout.LogoutSuccessHandler
    com.liaoin.security.pc.logout.LiaoinLogoutSuccessHandler
7. 短信发送的处理策略
    com.liaoin.security.core.validate.service.impl.sms.SmsCodeSender
8. 向sping容器注册名为：imageValidateCodeGenerator的bean，可以替换默认的图片验证码生产的逻辑
    com.liaoin.security.core.validate.service.ValidateCodeGenerator
9. 向sping容器注册名为：SmsCodeGenerator的bean，可以替换默认的短信验证码生产的逻辑
    com.liaoin.security.core.validate.service.ValidateCodeGenerator
10. spring容器中如有下面接口的实现，第三方用户无法确认系统用户时，此接口会自动注册用户
    org.springframework.social.connect.ConnectionSignUp
    如DemoConnectionSignUp
~~~
- 怎么用该安全模块   
    - 引入依赖
    ~~~
        <-- PC端依赖 -->
        <dependency>
            <groupId>com.liaoin.security</groupId>
            <artifactId>liaoin-security-pc</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <-- APP端依赖 -->
        <dependency>
            <groupId>com.liaoin.security</groupId>
            <artifactId>liaoin-security-app</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        
        <-- 当前你的系统spring版本应该正式发布版本v1.5.13 或者直接使用spring全家桶如下-->
        <dependencyManagement>
                <dependencies>
                    <dependency>
                        <groupId>io.spring.platform</groupId>
                        <artifactId>platform-bom</artifactId>
                        <version>Brussels-SR12</version>
                        <type>pom</type>
                        <scope>import</scope>
                    </dependency>
                    <dependency>
                        <groupId>org.springframework.cloud</groupId>
                        <artifactId>spring-cloud-dependencies</artifactId>
                        <version>Dalston.SR5</version>
                        <type>pom</type>
                        <scope>import</scope>
                    </dependency>
                </dependencies>
            </dependencyManagement>
        
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-compiler-plugin</artifactId>
                        <version>2.3.2</version>
                        <configuration>
                            <source>1.8</source>
                            <target>1.8</target>
                            <encoding>UTF-8</encoding>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
            
    ~~~
    - 需要在springBoot启动类中添加如下注解
    ~~~
        @ComponentScan(value = {"com.liaoin.security","您自己项目启动类扫描"})
    ~~~
    - 配置系统参见配置说明
    - 增加UserDetailsService接口实现
    - 如果需要登陆“记住我”功能，需要创建数据库表，参见sql
    - 如果需要第三方应用微信登陆、QQ登陆，需要额外配置相关参数
        - 配置appId、appSecret
        - 创建并配置用户注册页面，实现注册服务，在服务中必须调用ProviderSignInUtils的doPostSignUp方法
        - 添加SocialUserDetailsService接口实现
        - 创建社交登陆用的表，参见sql


#### **2018-09-24 更新**
- 重构第三方登陆方式 
    - 新增App通过三方账户登陆
    - 重新构建三方账户注册
- 完成App三种登陆方式 
    - 账户密码
    - 短信登陆
    - 三方账户登陆
- App与后台交互方式改为:`令牌`即OAuth2
    - 改变令牌的存储方式为redis
    - 新增多应用访问方式
- 使用JWT替換springSecurity默認的令牌  
    - 支持數據庫方式自由獲取權限
    
#### **2018-09-23 更新**
- 新增移动端获取三方openid登陆

#### **2018-09-18 更新**
1. 重新构建验证码服务
2. 支持App、PC端验证码分别获取
3. 支持App短信登陆
4. PC端session的key前置可配置


#### **2018-09-16 更新**
1. 创建spring默认认证服务器服务
2. 创建自定义资源访问服务器服务
3. 新建App资源目录
4. 构建基于`springSecurityOAuth2` 登录
5. 重构基于App用户名密码登录
6. 定义头部Authorization访问资源服务，类型为自定义：custom


#### **2018-08-11 更新**
1. 支持Sercurity自定义配置
2. 支持第三方登陆，目前只包含：QQ、微信
3. 支持手机号码短信登陆
4. 支持登陆：图片验证码、短信验证码自定义配置
5. 支持单应用、集群应用Session会话统一管理
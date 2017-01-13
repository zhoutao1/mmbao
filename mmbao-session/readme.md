# 买卖宝 分布式session
pom引入
```
<dependency>
    <groupId>com.mmbao.core</groupId>
    <artifactId>session</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-redis</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```



## 使用说明
### 1. spring 配置(启用redis存储模式)

```
<!-- 确保扫描的路径包括 com.mmbao.session -->
<context:component-scan base-package="com.mmbao" />

<!-- redis 配置 -->
<bean id="poolConfig4session" class="redis.clients.jedis.JedisPoolConfig">
	<property name="maxIdle">
		<value>${redis.maxIdle}</value>
	</property>
	<property name="maxTotal">
		<value>${redis.maxTotal}</value>
	</property>
	<property name="maxWaitMillis">
		<value>${redis.maxWaitMillis}</value>
	</property>
	<property name="testOnBorrow">
		<value>${redis.testOnBorrow}</value>
	</property>
</bean>

<bean id="jedisConnectionFactory4session"
	  class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
	<property name="hostName">
		<value>172.21.99.126</value>
	</property>
	<property name="port">
		<value>6379</value>
	</property>
	<property name="poolConfig" ref="poolConfig4session" />
</bean>

<bean id="sessionTemplate" class="org.springframework.data.redis.core.RedisTemplate">
	<property name="connectionFactory" ref="jedisConnectionFactory4session" />
	<property name="keySerializer">
		<bean class="org.springframework.data.redis.serializer.StringRedisSerializer" />
	</property>
	<property name="hashKeySerializer">
		<bean class="org.springframework.data.redis.serializer.StringRedisSerializer" />
	</property>
</bean>

<bean id="redisStorage" class="com.mmbao.session.storage.RedisStorage">
	<property name="template" ref="sessionTemplate" />
</bean>

<!-- 客户端可以去掉task注册 -->
<task:annotation-driven/>
```

### 2. filter配置(过滤器链的第一个)
```
<filter>
     <filter-name>sessionFilter</filter-name>
     <filter-class>com.mmbao.session.MmbaoSessionFilter</filter-class>
     <init-param>
         <param-name>session_type</param-name>
         <param-value>RedisSessionWrapper</param-value>
     </init-param>
</filter>
<filter-mapping>
    <filter-name>sessionFilter</filter-name>
    <url-pattern>*.html</url-pattern>
    <url-pattern>/j_spring_cas_security_check</url-pattern>
    <url-pattern>/j_spring_cas_security_logout</url-pattern>
</filter-mapping>

```

## 调用方式
### 1. 与httpsession相同
### 2. SessionId 从cookie获取,默认域名.mmbao.com(mmbao_session_id)
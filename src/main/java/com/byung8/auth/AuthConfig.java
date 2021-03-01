package com.byung8.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

import com.byung8.common.rest.RestClientTemplate;

import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableRedisRepositories
public class AuthConfig {

	private static String redisServer;
	private static int redisPort;
	private static String password;
	private static int connectionTimeout;
	private static int readTimeout;
	
	@Value("${rest.timeout.connection}")
	public void setConnectionTimeout(int timeout) {
		connectionTimeout = timeout;
	}
	
	@Value("${rest.timeout.read}")
	public void setReadTimeout(int timeout) {
		readTimeout = timeout;
	}
		
	@Value("${redis.server}")
	public void setRedisServer(String server) {
		redisServer = server;
	}
	public String getRedisServer() {
		return redisServer;
	}
	
	@Value("${redis.port}")
	public void setRedisPort(int port) {
		redisPort = port;
	}
	public int getRedisPort() {
		return redisPort;
	}
	
	@Value("${redis.password}")
	public void setPassword(String p) {
		password = p;
	}
	public String getPassword() {
		return password;
	}
	
	@Bean
	public JedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisServer, redisPort);
		config.setPassword(password);
	    return new JedisConnectionFactory(config);
	}
	
	@Bean(name="redisTemplate")
    public RedisTemplate redisTemplateConfig(JedisConnectionFactory jedisConnectionConfig) {
        
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
 
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(jedisConnectionConfig);
        
        return redisTemplate;
        
    }
	
	@Bean(name="restTemplate")
	public RestTemplate restTemplate() {
		return new RestClientTemplate(connectionTimeout, readTimeout);
	}
}

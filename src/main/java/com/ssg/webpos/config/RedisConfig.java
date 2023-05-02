package com.ssg.webpos.config;

import com.ssg.webpos.dto.CartDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Slf4j
public class RedisConfig {

  private final RedisProperties redisProperties;

  public RedisConfig(RedisProperties redisProperties) {
    this.redisProperties = redisProperties;
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(redisProperties.getHost());
    redisStandaloneConfiguration.setPort(redisProperties.getPort());

    LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
    return lettuceConnectionFactory;
  }

  @Bean
  public RedisTemplate<String, CartDto> redisTemplate() {
    RedisTemplate<String, CartDto> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    return redisTemplate;
  }
}
//  private String redisHost;
//  private int redisPort;
//
//  @Value("${spring.redis.host}")
//  private void setRedisHost(String redisHost){
//    this.redisHost = redisHost;
//  }
//  @Value("${spring.redis.port}")
//  private void setRedisPort(int redisPort){
//    this.redisPort = redisPort;
//  }
//
//  @Bean
//  public RedisConnectionFactory redisConnectionFactory() {
//    return new LettuceConnectionFactory(redisHost, redisPort);
//  }
//
//  @Bean
//  public <K, V> RedisTemplate<K, V> redisTemplate() {
//    RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
//    redisTemplate.setKeySerializer(new StringRedisSerializer());
//    redisTemplate.setValueSerializer(new StringRedisSerializer());
//    redisTemplate.setConnectionFactory(redisConnectionFactory());
//
//    return redisTemplate;
//  }
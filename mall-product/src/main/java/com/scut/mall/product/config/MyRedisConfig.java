package com.scut.mall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedisConfig {

	@Value("${ipAddr}")
	private String ipAddr;

	/**
	 * 所有对Redisson的使用都是通过RedissonClient对象
	 * @return
	 */
	@Bean(destroyMethod = "shutdown")
	public RedissonClient redisson() {
		// 创建配置
		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + ipAddr + ":6379");
		return Redisson.create(config);
	}
}

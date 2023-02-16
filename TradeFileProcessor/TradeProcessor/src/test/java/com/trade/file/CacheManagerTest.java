//package com.trade.file;
//
//
//import org.junit.jupiter.api.Test;
//
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.caffeine.CaffeineCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = CacheConfig.class)
//public class CacheManagerTest {
//
//    @Autowired
//    private CacheManager cacheManager;
//
//    @Test
//    public void testCacheManagerNotNull() {
//        assertNotNull(cacheManager);
//    }
//}
//
//@Configuration
//class CacheConfig {
//
//    @Bean
//    public CacheManager cacheManager() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager("caffeineCacheManager");
//        return cacheManager;
//    }
//}
//

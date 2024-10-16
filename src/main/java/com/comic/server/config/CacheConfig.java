package com.comic.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
@ImportAutoConfiguration({
  CacheAutoConfiguration.class,
})
@RequiredArgsConstructor
public class CacheConfig {

  private final ObjectMapper objectMapper;

  // // https://stackoverflow.com/questions/77973235/why-is-bucket4j-not-working-with-caffeine
  // // Implementing a cache manager for bucket4j
  // @Bean
  // @Lazy
  // public javax.cache.CacheManager cacheManagerForBucket() {
  //   CacheManagerImpl impl =
  //       new CacheManagerImpl(
  //           new CaffeineCachingProvider(),
  //           false,
  //           URI.create("com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider"),
  //           Thread.currentThread().getContextClassLoader(),
  //           new Properties());
  //   CaffeineConfiguration<Object, Object> configuration =
  //       new CaffeineConfiguration<Object, Object>();
  //   configuration.setExpireAfterAccess(
  //       OptionalLong.of(TimeUnit.NANOSECONDS.convert(259200, TimeUnit.SECONDS)));
  //   impl.createCache(
  //       "rate-limit-filter", configuration); // the cache for dynamic rate limit configiration
  //   impl.createCache("rate-limit-buckets", configuration); // the cache for bucket4j
  //   return impl;
  // }

  @Primary
  @Bean("caffeineCacheManager")
  public CacheManager caffeineCacheManager() {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("comics", "comic");
    caffeineCacheManager.setCaffeineSpec(
        CaffeineSpec.parse("maximumSize=3000,expireAfterAccess=30000s"));

    // caffeineCacheManager.registerCustomCache(
    //     "productCategories",
    //     Caffeine.newBuilder()
    //         .expireAfterWrite(Duration.ofDays(4))
    //         .initialCapacity(1)
    //         .maximumSize(30)
    //         .build());

    return caffeineCacheManager;
  }

  // @Bean
  // public RedisCacheConfiguration redisCacheConfiguration() {
  //   return RedisCacheConfiguration.defaultCacheConfig()
  //       .entryTtl(Duration.ofMinutes(5))
  //       .disableCachingNullValues()
  //       .serializeValuesWith(
  //           RedisSerializationContext.SerializationPair.fromSerializer(
  //               new GenericJackson2JsonRedisSerializer(objectMapper)));
  // }

  // @Bean("redisCacheManager")
  // public CacheManager redisCacheManager(
  //     RedisConnectionFactory connectionFactory, RedisCacheConfiguration cacheConfiguration) {

  //   return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory)
  //       .withCacheConfiguration(
  //           "productCategories", cacheConfiguration.entryTtl(Duration.ofDays(1)))
  //       .withCacheConfiguration("product", cacheConfiguration)
  //       .withCacheConfiguration("products", cacheConfiguration)
  //       .withCacheConfiguration("shop", cacheConfiguration)
  //       .withCacheConfiguration("shops", cacheConfiguration)
  //       .build();
  // }

  // @Bean
  // public static CacheInterceptor cacheInterceptor(
  //     CacheManager caffeineCacheManager, CacheOperationSource cacheOperationSource) {
  //   CacheInterceptor interceptor = new TwoLevelCacheInterceptor(caffeineCacheManager);
  //   interceptor.setCacheOperationSources(cacheOperationSource);
  //   return interceptor;
  // }

  // @Bean
  // public static CacheOperationSource cacheOperationSource() {
  //
  //   return new AnnotationCacheOperationSource();
  // }
}

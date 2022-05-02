package io.hiyenwong.spring.boot.autoconfigure;

import io.hiyenwong.spring.boot.MinioConstants;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.hiyenwong.spring.boot.MinioConstants.MINIO_TASK_EXECUTOR_BEAN_NAME;

/**
 * Minio AutoConfiguration
 *
 * @author Hi Yen Wong
 * @date 2022/5/2 12:24
 */
@ConditionalOnClass(MinioClient.class)
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = MinioConstants.ENABLED, havingValue = "true", matchIfMissing = true)
public class MinioAutoConfiguration {

  @Bean(name = MINIO_TASK_EXECUTOR_BEAN_NAME)
  @ConditionalOnMissingBean
  public ExecutorService minioTaskExecutor() {
    int coreSize = Runtime.getRuntime().availableProcessors();
    return new ThreadPoolExecutor(coreSize, 128, 60, TimeUnit.SECONDS, new SynchronousQueue<>());
  }
}

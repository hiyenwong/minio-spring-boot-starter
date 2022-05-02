package io.hiyenwong.spring.boot.config;

import io.hiyenwong.spring.boot.env.MinioProperties;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.hiyenwong.spring.boot.constants.MinioConstants.ENABLED;
import static io.hiyenwong.spring.boot.constants.MinioConstants.MINIO_TASK_EXECUTOR_BEAN_NAME;
import static org.yaml.snakeyaml.nodes.Tag.PREFIX;

/**
 * @author Hi Yen Wong
 * @date 2022/5/2 20:03
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MinioProperties.class)
@ConditionalOnProperty(prefix = PREFIX, name = ENABLED, havingValue = "true", matchIfMissing = true)
public class MinioAutoContextConfiguration {

  @Bean(name = MINIO_TASK_EXECUTOR_BEAN_NAME)
  @ConditionalOnMissingBean
  public ExecutorService ossTaskExecutor() {
    int coreSize = Runtime.getRuntime().availableProcessors();
    return new ThreadPoolExecutor(coreSize, 128, 60, TimeUnit.SECONDS, new SynchronousQueue<>());
  }

  @Bean
  public MinioClient minioClient(MinioProperties minioProperties) {
    Assert.isTrue(
        !StringUtils.isEmpty(minioProperties.getAccessKey()), "Minio Endpoint can't be empty. ");
    Assert.isTrue(
        !StringUtils.isEmpty(minioProperties.getSecretKey()), "Minio Endpoint can't be empty. ");
    Assert.isTrue(
        !StringUtils.isEmpty(minioProperties.getEndpoint()), "Minio Endpoint can't be empty. ");
    return MinioClient.builder()
        .endpoint(minioProperties.getEndpoint())
        .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
        .build();
  }
}

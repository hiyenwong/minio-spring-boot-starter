package io.hiyenwong.spring.boot.env;

import io.hiyenwong.spring.boot.MinioConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Hi Yen Wong
 * @date 2022/5/2 14:44
 */
@ConfigurationProperties(MinioConstants.PREFIX)
public class MinioProperties {
  private String endpoint;
}

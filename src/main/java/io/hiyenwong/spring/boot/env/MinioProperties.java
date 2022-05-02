package io.hiyenwong.spring.boot.env;

import com.alibaba.cloud.context.utils.StringUtils;
import io.hiyenwong.spring.boot.constants.MinioConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Hi Yen Wong
 * @date 2022/5/2 20:15
 */
@Component
@ConfigurationProperties(MinioConstants.PREFIX)
public class MinioProperties {
  private String endpoint;
  private String accessKey;

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public String getFileHost() {
    return fileHost;
  }

  public void setFileHost(String fileHost) {
    if (StringUtils.isNotEmpty(fileHost)) {
      fileHost = this.getEndpoint();
    }
    this.fileHost = fileHost;
  }

  private String secretKey;
  private String fileHost;
}

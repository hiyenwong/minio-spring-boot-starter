package io.hiyenwong.spring.boot.constants;

/**
 * @author Hi Yen Wong
 * @date 2022/5/2 20:12
 */
public final class MinioConstants {

  /** Prefix of MinioConfigurationProperties. */
  public static final String PREFIX = "application.minio";
  /** Enable Minio. */
  public static final String ENABLED = PREFIX + ".enabled";

  public static  String SLASH = "/";

  private MinioConstants() {
    throw new AssertionError("Must not instantiate constant utility class");
  }
}

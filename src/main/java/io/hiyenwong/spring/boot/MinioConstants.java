package io.hiyenwong.spring.boot;

/**
 * minio constants
 *
 * @author Hi Yen Wong
 * @date 2022/5/2 13:41
 */
public final class MinioConstants {
  public static final String PREFIX = "application.minio";
  public static final String ENABLED = PREFIX + ".enabled";
  public static final String MINIO_TASK_EXECUTOR_BEAN_NAME = "minioTaskExecutor";

  private MinioConstants() {
    throw new AssertionError("Must not instantiate constant utility class");
  }
}

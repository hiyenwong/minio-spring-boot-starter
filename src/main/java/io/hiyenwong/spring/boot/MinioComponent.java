package io.hiyenwong.spring.boot;

import com.google.common.io.Files;
import io.hiyenwong.spring.boot.constants.MinioConstants;
import io.hiyenwong.spring.boot.dto.UploadFileDto;
import io.hiyenwong.spring.boot.env.MinioProperties;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Component public class MinioComponent
 *
 * @author Hi Yen Wong
 * @date 2022/5/2 20:55
 */
@Component
public class MinioComponent {
  private static final Log LOG = LogFactory.getLog(MinioComponent.class);

  private final MinioClient minioClient;
  private final MinioProperties minioProperties;

  @Autowired
  public MinioComponent(MinioClient minioClient, MinioProperties minioProperties) {
    this.minioClient = minioClient;
    this.minioProperties = minioProperties;
  }

  /**
   * 创建bucket
   *
   * @param bucketName bucket名称
   * @throws Exception exception
   */
  public void createBucket(String bucketName) throws Exception {
    if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
    }
  }

  /**
   * upload file
   *
   * @param path path
   * @param fileName file name
   * @param bucketName bucket名称
   * @param uploadPath upload path
   * @param contentType contentType
   * @return File Upload dto
   * @throws Exception exception
   */
  public UploadFileDto uploadFile(
      String path, String fileName, String bucketName, String uploadPath, String contentType)
      throws Exception {
    File source = new File(path);
    String uploadFile = fileName;
    if (null != uploadPath) {
      uploadFile = uploadPath + MinioConstants.SLASH + fileName;
    }
    createBucket(bucketName);
    InputStream inputStream = Files.asByteSource(source).openStream();
    minioClient.putObject(
        PutObjectArgs.builder().bucket(bucketName).object(uploadFile).stream(
                inputStream, -1, 10485760)
            .contentType(contentType)
            .build());
    return new UploadFileDto(
        minioProperties.getEndpoint() + MinioConstants.SLASH + bucketName + uploadFile,
        MinioConstants.SLASH + bucketName + uploadPath,
        fileName);
  }

  public UploadFileDto uploadFile(
      File file, String bucketName, String uploadPath, String fileName, String contentType)
      throws Exception {
    String uploadFile = fileName;
    if (!StringUtils.isEmpty(uploadPath)) {
      uploadFile = uploadPath + MinioConstants.SLASH + fileName;
    }
    createBucket(bucketName);
    InputStream inputStream = Files.asByteSource(file).openStream();
    minioClient.putObject(
        PutObjectArgs.builder().bucket(bucketName).object(uploadFile).stream(
                inputStream, -1, 10485760)
            .contentType(contentType)
            .build());

    return new UploadFileDto(
        minioProperties.getEndpoint()
            + MinioConstants.SLASH
            + bucketName
            + MinioConstants.SLASH
            + uploadFile,
        uploadPath,
        fileName);
  }

  /**
   * 上传文件
   *
   * @param file file
   * @param bucketName bucket名称
   * @return file upload response
   * @throws Exception exception
   */
  public UploadFileDto uploadFile(MultipartFile file, String bucketName) throws Exception {
    // 判断文件是否为空
    if (null == file || 0 == file.getSize()) {
      return null;
    }
    // 判断存储桶是否存在  不存在则创建
    createBucket(bucketName);
    // 文件名
    String originalFilename = file.getOriginalFilename();
    // 新的文件名 = 存储桶文件名_时间戳.后缀名
    assert originalFilename != null;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String fileName =
        bucketName
            + "_"
            + System.currentTimeMillis()
            + "_"
            + format.format(new Date())
            + "_"
            + new Random().nextInt(1000)
            + originalFilename.substring(originalFilename.lastIndexOf("."));
    // 开始上传
    minioClient.putObject(
        PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                file.getInputStream(), file.getSize(), -1)
            .contentType(file.getContentType())
            .build());
    String url = minioProperties.getEndpoint() + "/" + bucketName + "/" + fileName;
    String urlHost = minioProperties.getFileHost() + "/" + bucketName + "/" + fileName;
    LOG.info("上传文件成功url ：[{" + url + "}], urlHost ：[" + urlHost + "]");
    return new UploadFileDto(url, urlHost, fileName);
  }

  /**
   * 获取全部bucket
   *
   * @return list bucket
   */
  public List<Bucket> getAllBuckets() throws Exception {
    return minioClient.listBuckets();
  }

  /**
   * 根据bucketName获取信息
   *
   * @param bucketName bucket名称
   */
  public Optional<Bucket> getBucket(String bucketName)
      throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException,
          InvalidResponseException, InternalException, ErrorResponseException, ServerException,
          XmlParserException {
    return minioClient.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
  }

  /**
   * 根据bucketName删除信息
   *
   * @param bucketName bucket名称
   */
  public void removeBucket(String bucketName) throws Exception {
    minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
  }

  /**
   * 获取⽂件外链
   *
   * @param bucketName bucket名称
   * @param objectName ⽂件名称
   * @param expires 过期时间 <=7
   * @return url
   */
  public String getObjectURL(String bucketName, String objectName, Integer expires)
      throws Exception {
    return minioClient.getPresignedObjectUrl(
        GetPresignedObjectUrlArgs.builder()
            .bucket(bucketName)
            .object(objectName)
            .expiry(expires)
            .build());
  }

  /**
   * 获取⽂件
   *
   * @param bucketName bucket名称
   * @param objectName ⽂件名称
   * @return ⼆进制流
   */
  public InputStream getObject(String bucketName, String objectName) throws Exception {
    return minioClient.getObject(
        GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
  }

  /**
   * putObject
   *
   * @param bucketName bucket名称
   * @param objectName ⽂件名称
   * @param stream ⽂件流
   * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
   */
  public void putObject(String bucketName, String objectName, InputStream stream) throws Exception {
    minioClient.putObject(
        PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                stream, stream.available(), -1)
            .contentType(objectName.substring(objectName.lastIndexOf(".")))
            .build());
  }

  /**
   * putObject
   *
   * @param bucketName bucket名称
   * @param objectName ⽂件名称
   * @param stream ⽂件流
   * @param size ⼤⼩
   * @param contextType 类型
   * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
   */
  public void putObject(
      String bucketName, String objectName, InputStream stream, long size, String contextType)
      throws Exception {
    minioClient.putObject(
        PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, size, -1)
            .contentType(contextType)
            .build());
  }

  /**
   * get object information
   *
   * @param bucketName bucket名称
   * @param objectName ⽂件名称
   * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#statObject
   */
  public StatObjectResponse getObjectInfo(String bucketName, String objectName) throws Exception {
    return minioClient.statObject(
        StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
  }

  /**
   * remove object
   *
   * @param bucketName bucketName
   * @param objectName objectName
   * @throws Exception https://docs.minio.io/cn/java-client-apireference.html#removeObject
   */
  public void removeObject(String bucketName, String objectName) throws Exception {
    minioClient.removeObject(
        RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
  }
}

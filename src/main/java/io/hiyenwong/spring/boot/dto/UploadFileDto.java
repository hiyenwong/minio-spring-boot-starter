package io.hiyenwong.spring.boot.dto;

/**
 * @author Hi Yen Wong
 * @date 2022/5/2 21:42
 */
public class UploadFileDto {
  public UploadFileDto(String fullUrl, String path, String fileName) {
    this.fullUrl = fullUrl;
    this.path = path;
    this.fileName = fileName;
  }

  public UploadFileDto() {

  }

  public String getFullUrl() {
    return fullUrl;
  }

  public void setFullUrl(String fullUrl) {
    this.fullUrl = fullUrl;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  private String fullUrl;
  private String path;
  private String fileName;
}

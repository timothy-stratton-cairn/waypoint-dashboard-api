package com.cairn.waypoint.dashboard.utility.fileupload;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3FileUpload implements FileUpload {

  private final AmazonS3 s3Client;

  @Value("${waypoint.dashboard.s3.bucket}")
  private String bucketName;

  public S3FileUpload(AmazonS3 s3Client) {
    this.s3Client = s3Client;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Override
  public String uploadFile(MultipartFile multipartFile, String uploader, String baseKey)
      throws IOException {
    // convert multipart file  to a file
    File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      fileOutputStream.write(multipartFile.getBytes());
    }

    // generate file name
    String fileName = generateFileName(multipartFile, uploader, baseKey);

    // upload file
    PutObjectRequest request = new PutObjectRequest(this.bucketName, fileName, file);
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(
        "plain/" + FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
    metadata.addUserMetadata("Title", "File Upload - " + fileName);
    metadata.setContentLength(file.length());
    request.setMetadata(metadata);
    s3Client.putObject(request);

    // delete file
    file.delete();

    return fileName;
  }

  @Override
  public Object downloadFile(String fileName) throws IOException {
    return null;
  }

  @Override
  public boolean delete(String fileName) {
    return false;
  }

  private String generateFileName(MultipartFile multiPart, String uploader, String baseKey) {
    return baseKey + new Date().getTime() + "-"
        + uploader + "-"
        + Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
  }
}

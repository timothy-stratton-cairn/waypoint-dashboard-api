package com.cairn.waypoint.dashboard.utility.fileupload;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {

  String uploadFile(MultipartFile multipartFile, String uploader, String baseKey)
      throws IOException;

  Object downloadFile(String fileName, String baseKey) throws IOException;

  boolean delete(String fileName);
}

package com.cairn.waypoint.dashboard.utility.fileupload;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {

  String uploadFile(MultipartFile multipartFile, String uploader) throws IOException;

  Object downloadFile(String fileName) throws IOException;

  boolean delete(String fileName);
}

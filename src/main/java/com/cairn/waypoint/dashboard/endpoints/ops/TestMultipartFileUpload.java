package com.cairn.waypoint.dashboard.endpoints.ops;

import com.cairn.waypoint.dashboard.endpoints.homework.UpdateHomeworkResponsesEndpoint;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.UpdateHomeworkResponseDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.UpdateHomeworkResponseDetailsListDto;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestMultipartFileUpload {
  public static final String PATH = "/api/ops/test-multipart-file-upload";

  private final RestTemplate restTemplate;

  @Value("${waypoint.dashboard.base-url}")
  private String baseUrl;

  public TestMultipartFileUpload(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @PostMapping(PATH)
  @PreAuthorize("hasAuthority('SCOPE_admin.full')")
  public void testUpdateHomeworkResponses() throws IOException {
    //#### HERE YOU GRAB THE CORRECT DETAILS FROM THE FILE TO BE UPLOADED ####
    final String filename = "What-Does-it-Mean-if-My-Drivers-License-is-On-Hold-in-Illinois.jpg";
    InputStream is = this.getClass().getClassLoader().getResourceAsStream("static/images/What-Does-it-Mean-if-My-Drivers-License-is-On-Hold-in-Illinois.jpg");

    MultiValueMap<String,Object> multipartRequest = new LinkedMultiValueMap<>();

    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

    //#### HERE YOU ADD YOUR ACCESS TOKEN ####
    requestHeaders.setBearerAuth(((Jwt) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal()).getTokenValue());

    HttpHeaders requestHeadersAttachment = new HttpHeaders();;
    requestHeadersAttachment.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    HttpEntity<ByteArrayResource> attachmentPart;
    ByteArrayResource fileAsResource = new ByteArrayResource(Objects.requireNonNull(is).readAllBytes()){
      @Override
      public String getFilename(){
        return filename;
      }
    };
    attachmentPart = new HttpEntity<>(fileAsResource, requestHeadersAttachment);

    multipartRequest.set("files", attachmentPart);

    //#### HERE YOU WAS MY BIGGEST HURDLE - THIS APPROACH REQUIRES THE JSON PART OF THE MULTIPART/FORM_DATA ####
    //#### TO BE THE EXACT REPRESENTATION OF THE SERVER-SIDE DTO. IT DIDN'T WORK FOR ME OTHERWISE ####
    HttpHeaders requestHeadersJSON = new HttpHeaders();
    requestHeadersJSON.setContentType(MediaType.APPLICATION_JSON);
    UpdateHomeworkResponseDetailsListDto requestBody = UpdateHomeworkResponseDetailsListDto.builder()
        .responses(List.of(UpdateHomeworkResponseDetailsDto.builder()
          .questionId(8L)
          .userResponse(filename)
          .build()))
        .build();
    HttpEntity<UpdateHomeworkResponseDetailsListDto> requestEntityJSON = new HttpEntity<>(requestBody, requestHeadersJSON);

    multipartRequest.set("json",requestEntityJSON);

    HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(multipartRequest, requestHeaders);//final request

    String url = baseUrl + UpdateHomeworkResponsesEndpoint.PATH.replace("{homeworkId}", "2");
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, String.class);

    System.out.println(response);
  }
}

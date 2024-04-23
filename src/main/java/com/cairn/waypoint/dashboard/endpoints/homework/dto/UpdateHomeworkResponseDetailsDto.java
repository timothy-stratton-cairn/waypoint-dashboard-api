package com.cairn.waypoint.dashboard.endpoints.homework.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHomeworkResponseDetailsDto {

  private Long questionId;
  private String userResponse;
  @JsonIgnore
  private MultipartFile uploadedFile;
}



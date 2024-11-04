package com.cairn.waypoint.dashboard.endpoints.homework.dto;

import java.util.List;

//import com.cairn.waypoint.dashboard.entity.Homework;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHomeworkResponseDetailsListDto {

  private List<UpdateHomeworkResponseDetailsDto> responses;
  
}

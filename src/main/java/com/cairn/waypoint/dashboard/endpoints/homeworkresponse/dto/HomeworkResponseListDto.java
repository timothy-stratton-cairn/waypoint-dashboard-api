package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import java.util.List;

import com.cairn.waypoint.dashboard.entity.HomeworkResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeworkResponseListDto{
	List<HomeworkResponse> responses;
	private Integer numberOfResponses;
	
    public Integer getNumOfResponses() {
	    return responses.size();
	  }
}
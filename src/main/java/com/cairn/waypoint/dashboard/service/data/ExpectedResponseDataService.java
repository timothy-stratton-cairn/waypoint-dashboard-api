package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.repository.ExpectedResponseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ExpectedResponseDataService {

  private final ExpectedResponseRepository expectedResponseRepository;

  public ExpectedResponseDataService(ExpectedResponseRepository ExpectedResponseRepository) {
    this.expectedResponseRepository = ExpectedResponseRepository;
  }

  public Optional<ExpectedResponse> getExpectedResponseByResponse(String response) {
    return this.expectedResponseRepository.findExpectedResponseByResponse(response);
  }

  public List<ExpectedResponse> getAllExpectedResponses() {
    return this.expectedResponseRepository.findAll();
  }

  public Optional<ExpectedResponse> getExpectedResponseById(Long id) {
    return this.expectedResponseRepository.findById(id);
  }

  public ExpectedResponse saveExpectedResponse(ExpectedResponse ExpectedResponse) {
    return this.expectedResponseRepository.save(ExpectedResponse);
  }
}

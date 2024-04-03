package com.cairn.waypoint.dashboard.service;

import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.repository.ExpectedResponseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ExpectedResponseService {

  private final ExpectedResponseRepository expectedResponseRepository;

  public ExpectedResponseService(ExpectedResponseRepository ExpectedResponseRepository) {
    this.expectedResponseRepository = ExpectedResponseRepository;
  }

  public List<ExpectedResponse> getAllExpectedResponses() {
    return this.expectedResponseRepository.findAll();
  }

  public Optional<ExpectedResponse> getExpectedResponseById(Long id) {
    return this.expectedResponseRepository.findById(id);
  }

  public Long saveExpectedResponse(ExpectedResponse ExpectedResponse) {
    return this.expectedResponseRepository.save(ExpectedResponse).getId();
  }
}

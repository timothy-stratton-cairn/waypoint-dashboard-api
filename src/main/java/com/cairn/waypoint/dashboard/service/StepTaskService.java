package com.cairn.waypoint.dashboard.service;


import com.cairn.waypoint.dashboard.entity.StepTask;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.repository.StepTaskRepository;
import com.cairn.waypoint.dashboard.repository.StepTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StepTaskService {

  private final StepTaskRepository stepTaskRepository;

  public StepTaskService(StepTaskRepository stepTaskRepository) {
    this.stepTaskRepository = stepTaskRepository;
  }

  public List<StepTask> getAllStepTasks() {
    return this.stepTaskRepository.findAll();
  }

  public Optional<StepTask> getStepTaskById(Long id) {
    return this.stepTaskRepository.findById(id);
  }

  public Long saveStepTask(StepTask stepTask) {
    return this.stepTaskRepository.save(stepTask).getId();
  }

  public Optional<StepTask> findStepTaskByName(String name) {
    return this.stepTaskRepository.findByName(name);
  }
}

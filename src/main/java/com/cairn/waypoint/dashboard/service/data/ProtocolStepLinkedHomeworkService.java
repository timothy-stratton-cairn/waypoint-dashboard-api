package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.ProtocolStepLinkedHomework;
import com.cairn.waypoint.dashboard.repository.ProtocolStepLinkedHomeworkRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProtocolStepLinkedHomeworkService {

  private final ProtocolStepLinkedHomeworkRepository protocolStepLinkedHomeworkRepository;

  public ProtocolStepLinkedHomeworkService(
      ProtocolStepLinkedHomeworkRepository protocolStepLinkedHomeworkRepository) {
    this.protocolStepLinkedHomeworkRepository = protocolStepLinkedHomeworkRepository;
  }

  public List<ProtocolStepLinkedHomework> getProtocolStepLinkedHomeworkByHomework(
      Homework homework) {
    return this.protocolStepLinkedHomeworkRepository.findByHomework(homework);
  }

  public ProtocolStepLinkedHomework saveProtocolStepLinkedHomework(
      ProtocolStepLinkedHomework protocolStepLinkedHomework) {
    return this.protocolStepLinkedHomeworkRepository.save(
        protocolStepLinkedHomework);
  }

  public List<ProtocolStepLinkedHomework> saveProtocolStepLinkedHomework(
      Collection<ProtocolStepLinkedHomework> protocolStepLinkedHomeworks) {
    return this.protocolStepLinkedHomeworkRepository.saveAll(
        protocolStepLinkedHomeworks);
  }
}

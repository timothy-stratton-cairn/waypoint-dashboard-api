package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestionLinkedProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionLinkedProtocolTemplatesRepository;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolTemplateRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class HomeworkQuestionDataService {

  private final HomeworkQuestionRepository homeworkQuestionRepository;
  private final HomeworkQuestionLinkedProtocolTemplatesRepository homeworkQuestionLinkedProtocolTemplatesRepository;
  private final ProtocolTemplateRepository protocolTemplateRepository;
  private final ProtocolRepository protocolRepository;

  public HomeworkQuestionDataService(HomeworkQuestionRepository homeworkQuestionRepository,
      ProtocolTemplateRepository protocolTemplateRepository,
      HomeworkQuestionLinkedProtocolTemplatesRepository homeworkQuestionLinkedProtocolTemplatesRepository,
      ProtocolRepository protocolRepository
  ) {
    this.homeworkQuestionRepository = homeworkQuestionRepository;
    this.protocolTemplateRepository = protocolTemplateRepository;
    this.homeworkQuestionLinkedProtocolTemplatesRepository = homeworkQuestionLinkedProtocolTemplatesRepository;
    this.protocolRepository = protocolRepository;
  }

  public List<HomeworkQuestion> getAllHomeworkQuestions() {
    return this.homeworkQuestionRepository.findAll();
  }

  public Optional<HomeworkQuestion> getHomeworkQuestionById(Long id) {
    return this.homeworkQuestionRepository.findById(id);
  }

  public HomeworkQuestion saveHomeworkQuestion(HomeworkQuestion homeworkQuestion) {
    return this.homeworkQuestionRepository.save(homeworkQuestion);
  }

  public List<HomeworkQuestion> findByTriggeredProtocol(ProtocolTemplate protocolTemplate) {
    return this.homeworkQuestionRepository.findByTriggeredProtocol(protocolTemplate);
  }

  public List<HomeworkQuestion> getHomeworkQuestionByCategory(Long categoryId) {
    return this.homeworkQuestionRepository.getHomeworkQuestionByCategory_Id(categoryId);
  }

  public Optional<HomeworkQuestion> findByQuestion(String question) {
    return this.homeworkQuestionRepository.findByQuestion(question);
  }

  public List<HomeworkQuestion> getHomeworkQuestionsByTemplateId(Long templateId) {
    List<HomeworkQuestionLinkedProtocolTemplate> linkedProtocolTemplates = homeworkQuestionLinkedProtocolTemplatesRepository.findByProtocolTemplate_Id(
        templateId);
    return linkedProtocolTemplates.stream()
        .map(HomeworkQuestionLinkedProtocolTemplate::getQuestion)
        .collect(Collectors.toList());
  }

  public List<HomeworkQuestion> getHomeworkQuestionsByProtocolId(Long protocolId) {
    Protocol protocol = protocolRepository.findById(protocolId).get();

    List<HomeworkQuestionLinkedProtocolTemplate> linkedProtocolTemplates =
        homeworkQuestionLinkedProtocolTemplatesRepository.findByProtocolTemplate_Id(
            protocol.getId());

    return linkedProtocolTemplates.stream()
        .map(HomeworkQuestionLinkedProtocolTemplate::getQuestion)
        .collect(Collectors.toList());
  }

  public void linkQuestionToProtocolTemplate(Long questionId, Long protocolTemplateId) {

    HomeworkQuestion question = homeworkQuestionRepository.findById(questionId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + questionId));
    ProtocolTemplate template = protocolTemplateRepository.findById(protocolTemplateId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Invalid protocol template ID: " + protocolTemplateId));

    HomeworkQuestionLinkedProtocolTemplate link = new HomeworkQuestionLinkedProtocolTemplate();
    link.setQuestion(question);
    link.setProtocolTemplate(template);

    homeworkQuestionLinkedProtocolTemplatesRepository.save(link);
  }

  public HomeworkQuestion getHomeworkQuestionByResponse(HomeworkResponse response) {
    return response.getHomeworkQuestion();
  }

}

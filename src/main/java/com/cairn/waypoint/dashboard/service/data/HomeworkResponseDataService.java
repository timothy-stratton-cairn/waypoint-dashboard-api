package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.HomeworkResponseDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.HomeworkResponseListDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionLinkedProtocolTemplatesRepository;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionRepository;
import com.cairn.waypoint.dashboard.repository.HomeworkResponseRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class HomeworkResponseDataService {

  private final HomeworkResponseRepository homeworkResponseRepository;
  private final ProtocolRepository protocolRepository;

  private final HomeworkQuestionLinkedProtocolTemplatesRepository linkedRepository;
  private final HomeworkQuestionRepository questionRepository;

  public HomeworkResponseDataService(
      HomeworkResponseRepository homeworkResponseRepository,
      ProtocolRepository protocolRepository,
      HomeworkQuestionRepository questionRepository,
      HomeworkQuestionLinkedProtocolTemplatesRepository linkedRepository
  ) {
    this.homeworkResponseRepository = homeworkResponseRepository;
    this.protocolRepository = protocolRepository;
    this.linkedRepository = linkedRepository;

    this.questionRepository = questionRepository;
  }

  public HomeworkResponse saveHomeworkResponse(HomeworkResponse homeworkResponse) {
    return this.homeworkResponseRepository.save(homeworkResponse);
  }

  public void saveAll(List<HomeworkResponse> homeworkResponses) {
    homeworkResponseRepository.saveAll(homeworkResponses);
  }

  public Optional<HomeworkResponse> getHomeworkResponseByFileGuid(String fileGuid) {
    return this.homeworkResponseRepository.findHomeworkResponseByFileGuid(fileGuid);
  }

  public List<HomeworkResponse> getAllHomeworkResponsesByHomeworkQuestion(
      HomeworkQuestion homeworkQuestion) {
    return this.homeworkResponseRepository.findHomeworkResponseByHomeworkQuestion(homeworkQuestion);
  }

  public List<HomeworkResponse> getHomeworkResponseByCategory(Long categoryId) {
    return this.homeworkResponseRepository.getHomeworkResponseByCategory_Id(categoryId);
  }

  public HomeworkResponseListDto getHomeResponseByProtocol_Id(Long protocolId) {
    Protocol protocol = protocolRepository.findById(protocolId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid protocol ID: " + protocolId));

    Long templateId = protocol.getProtocolTemplate().getId();
    List<Long> questionIds = linkedRepository.findByProtocolTemplate_Id(templateId)
        .stream()
        .map(link -> link.getQuestion().getId())
        .collect(Collectors.toList());

    List<HomeworkResponse> responses = homeworkResponseRepository.findByHomeworkQuestion_IdIn(
        questionIds);
    return HomeworkResponseListDto.builder()
        .responses(responses)
        .numberOfResponses(responses.size())
        .build();
  }

  public HomeworkResponse saveHomeworkResponseFromDto(HomeworkResponseDto dto) {
    HomeworkQuestion homeworkQuestion = questionRepository.findById(dto.getQuestionId())
        .orElseThrow(() -> new EntityNotFoundException("HomeworkQuestion not found"));

    HomeworkResponse homeworkResponse = HomeworkResponse.builder()
        .response(dto.getResponse())
        .homeworkQuestion(homeworkQuestion)
        .fileGuid(dto.getFileGuid())
        .active(true) // Set default value for active if needed
        .build();

    return homeworkResponseRepository.save(homeworkResponse);
  }

  public List<HomeworkResponse> getAllResponsesByUser_Id(Long userId) {
    return this.homeworkResponseRepository.findAllByUserId(userId);
  }

  <Optional> HomeworkResponse getHomeworkResponse(HomeworkQuestion homeworkQuestion) {
    return homeworkResponseRepository.findByHomeworkQuestion(homeworkQuestion);
  }


}

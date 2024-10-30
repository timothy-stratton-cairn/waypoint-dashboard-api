package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.HomeworkResponseDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestionLinkedProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.entity.HomeworkResponseLinkedProtocol;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionLinkedProtocolTemplatesRepository;
import com.cairn.waypoint.dashboard.repository.HomeworkResponseLinkedProtocolRepository;
import com.cairn.waypoint.dashboard.repository.HomeworkResponseRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class HomeworkResponseDataService {

  private final HomeworkResponseRepository homeworkResponseRepository;
  private final ProtocolRepository protocolRepository;
  private final HomeworkQuestionLinkedProtocolTemplatesRepository questionLinkRepository;

  public HomeworkResponseDataService(HomeworkResponseRepository homeworkResponseRepository,
          ProtocolRepository protocolRepository,
          HomeworkQuestionLinkedProtocolTemplatesRepository questionLinkRepository) {
	this.homeworkResponseRepository = homeworkResponseRepository;
	this.protocolRepository = protocolRepository;
	this.questionLinkRepository = questionLinkRepository;
	}

  public HomeworkResponse saveHomeworkResponse(HomeworkResponse homeworkResponse) {
    return this.homeworkResponseRepository.save(homeworkResponse);
  }

  public Optional<HomeworkResponse> getHomeworkResponseByFileGuid(String fileGuid) {
    return this.homeworkResponseRepository.findHomeworkResponseByFileGuid(fileGuid);
  }

  public List<HomeworkResponse> getAllHomeworkResponsesByHomeworkQuestion(
      HomeworkQuestion homeworkQuestion) {
    return this.homeworkResponseRepository.findHomeworkResponseByHomeworkQuestion(homeworkQuestion);
  }
  
  public List<HomeworkResponse> getHomeworkResponseByCategory(Long categoryId){      
  	  return this.homeworkResponseRepository.getHomeworkResponseByCategory_Id(categoryId);
    }
  public List<HomeworkResponse> getHomeResponseByProtocol_Id(Long protocolId) {
      Protocol protocol = protocolRepository.findById(protocolId)
          .orElseThrow(() -> new IllegalArgumentException("Invalid protocol ID: " + protocolId));
      Long templateId = protocol.getProtocolTemplate().getId();
      List<Long> questionIds = questionLinkRepository.findByProtocolTemplate_Id(templateId)
          .stream()
          .map(link -> link.getQuestion().getId())
          .collect(Collectors.toList());
      return homeworkResponseRepository.findByHomeworkQuestion_IdIn(questionIds);
  }
  

  public List<HomeworkResponseDto> getQuestionsAndResponsesByProtocolAndUser(Long protocolId) {
      // Retrieve protocol to get template ID and user ID
      Protocol protocol = protocolRepository.findById(protocolId)
          .orElseThrow(() -> new IllegalArgumentException("Invalid protocol ID: " + protocolId));
      Long templateId = protocol.getProtocolTemplate().getId();
      Long userId = protocol.getUserId();

      // Retrieve all question IDs associated with the template
      List<Long> questionIds = questionLinkRepository.findByProtocolTemplate_Id(templateId)
          .stream()
          .map(link -> link.getQuestion().getId())
          .collect(Collectors.toList());
      
      List<HomeworkResponseDto> resultList = new ArrayList<>();

      for (Long questionId : questionIds) {
          HomeworkQuestion question = questionLinkRepository.findByQuestion_Id(questionId);
    		  if (question == null) {
    			    throw new IllegalArgumentException("Invalid question ID: " + questionId);
    			}
          Optional<HomeworkResponse> responseOpt = homeworkResponseRepository
              .findByHomeworkQuestion_IdAndUserId(questionId, userId);

          HomeworkResponseDto dto = HomeworkResponseDto.builder()
              .questionId(question.getId())
              .responseId(responseOpt.map(HomeworkResponse::getId).orElse(null))
              .categroyId(question.getCategory().getId())
              .response(responseOpt.map(HomeworkResponse::getResponse).orElse(null))
              .file_guide(responseOpt.map(HomeworkResponse::getFileGuid).orElse(null))
              .build();

          resultList.add(dto);
      }

      return resultList;
  }
}

package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.HomeworkResponseDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.HomeworkResponseListDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestionLinkedProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.entity.HomeworkResponseLinkedProtocol;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionLinkedProtocolTemplatesRepository;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionRepository;
import com.cairn.waypoint.dashboard.repository.HomeworkResponseLinkedProtocolRepository;
import com.cairn.waypoint.dashboard.repository.HomeworkResponseRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
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
  
  public List<HomeworkResponse> getHomeworkResponseByCategory(Long categoryId){      
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
	    
	    List<HomeworkResponse> responses = homeworkResponseRepository.findByHomeworkQuestion_IdIn(questionIds);
	    return HomeworkResponseListDto.builder()
	        .responses(responses)
	        .numberOfResponses(responses.size())
	        .build();
	}
  
  //TODO This probably only needs to be getQuestionsAndResponsesByProtocol. Protocols will be tied to users 
  public List<HomeworkResponseDto> getQuestionsAndResponsesByProtocolAndUser(Long protocolId) {

      Protocol protocol = protocolRepository.findById(protocolId)
  
          .orElseThrow(() -> new IllegalArgumentException("Invalid protocol ID: " + protocolId));
      Long templateId = protocol.getProtocolTemplate().getId();

      List<Long> questionIds = linkedRepository.findByProtocolTemplate_Id(templateId)
          .stream()
          .map(link -> link.getQuestion().getId())
          .collect(Collectors.toList());
      
      List<HomeworkResponseDto> resultList = new ArrayList<>();

      for (Long questionId : questionIds) {
    	    HomeworkQuestion question = questionRepository.findById(questionId)
    	        .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));

    	    Optional<HomeworkResponse> responseOpt = homeworkResponseRepository
    	        .findByHomeworkQuestion_IdAndProtocol_Id(questionId, protocolId);

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

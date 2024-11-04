package com.cairn.waypoint.dashboard.service.data;


import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.AddLinkedQuestionProtocolTemplateDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestionLinkedProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionLinkedProtocolTemplatesRepository;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolTemplateRepository;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HomeworkQuestionLinkedProtocolTemplateDataService{
	private final HomeworkQuestionLinkedProtocolTemplatesRepository linkedRepository;
	private final HomeworkQuestionRepository questionRepository;
	private final ProtocolTemplateRepository templateRepository;

	public HomeworkQuestionLinkedProtocolTemplateDataService(
	            HomeworkQuestionLinkedProtocolTemplatesRepository linkedRepository,
	            HomeworkQuestionRepository questionRepository,
	            ProtocolTemplateRepository templateRepository) {
	        this.linkedRepository = linkedRepository;
	        this.questionRepository = questionRepository;
	        this.templateRepository = templateRepository;
	    }

	    // Method to create and save a new link between HomeworkQuestion and ProtocolTemplate
	 public HomeworkQuestionLinkedProtocolTemplate createLink(AddLinkedQuestionProtocolTemplateDto dto, String modifiedBy) {
	        HomeworkQuestion homeworkQuestion = questionRepository.findById(dto.getQuestionId())
	                .orElseThrow(() -> new IllegalArgumentException("Homework Question ID not found: " + dto.getQuestionId()));

	        ProtocolTemplate protocolTemplate = templateRepository.findById(dto.getTemplateId())
	                .orElseThrow(() -> new IllegalArgumentException("Protocol Template ID not found: " + dto.getTemplateId()));

	        HomeworkQuestionLinkedProtocolTemplate link = new HomeworkQuestionLinkedProtocolTemplate();
	        link.setQuestion(homeworkQuestion);
	        link.setProtocolTemplate(protocolTemplate);

	        return linkedRepository.save(link);
	    }
	 public List<HomeworkQuestion> findAllQuestionsByQuestionId(Long questionId) {
		    List<Long> questionIds = linkedRepository.findAllByQuestion_Id(questionId).stream()
		        .map(linkedTemplate -> linkedTemplate.getQuestion().getId()) 
		        .collect(Collectors.toList());
		    return this.questionRepository.findAllById(questionIds);		
		    }
	 
	
	public List<HomeworkQuestionLinkedProtocolTemplate> findAllByProtocolTemplate(
			Long templateId){
		return this.linkedRepository.findByProtocolTemplate_Id(templateId);
	}
	
	
}
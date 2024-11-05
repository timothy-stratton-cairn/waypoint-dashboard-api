package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.QuestionResponsePairDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.QuestionResponsePairListDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestionLinkedProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.entity.Protocol;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class QuestionResponsePairDataService {
    private final ProtocolDataService protocolService;
    private final HomeworkQuestionDataService questionService;
    private final HomeworkQuestionLinkedProtocolTemplateDataService questionLinkedProtocolTemplateDataServiceService;
    private final HomeworkResponseDataService responseService;
    private final HomeworkResponseDataService homeworkResponseDataService;

    public QuestionResponsePairDataService(
            ProtocolDataService protocolService, HomeworkQuestionDataService questionService,
            HomeworkQuestionLinkedProtocolTemplateDataService questionLinkedProtocolTemplateDataServiceService,
            HomeworkResponseDataService responseService, HomeworkResponseDataService homeworkResponseDataService){
        this.protocolService = protocolService;
        this.questionService = questionService;
        this.questionLinkedProtocolTemplateDataServiceService = questionLinkedProtocolTemplateDataServiceService;
        this.responseService = responseService;
        this.homeworkResponseDataService = homeworkResponseDataService;
    }

    public QuestionResponsePairListDto getQuestionResponsePairsByUserId(Long userId) {

        ArrayList<QuestionResponsePairDto> questionResponsePairs = new ArrayList<>();
        List<Protocol> protocols = protocolService.findByUserId(userId);
        for (Protocol protocol: protocols) {
            List<HomeworkQuestionLinkedProtocolTemplate> linkedQuestions = questionLinkedProtocolTemplateDataServiceService.findAllByProtocolTemplate(protocol.getProtocolTemplate().getId());
            for(HomeworkQuestionLinkedProtocolTemplate linkedQuestion: linkedQuestions) {
                HomeworkQuestion question = questionService.getHomeworkQuestionById(linkedQuestion.getId()).orElseThrow(() -> new EntityNotFoundException("Question not found"));

                List<HomeworkResponse> responses = responseService.getResponsesByProtocolAndQuestion(protocol, question);
                if (!responses.isEmpty()) {
                    for(HomeworkResponse response: responses){
                    questionResponsePairs.add(new QuestionResponsePairDto(protocol,question,response));
                    }
                } else {
                questionResponsePairs.add(new QuestionResponsePairDto(protocol, question, null));
            }
            }
        }


        return new QuestionResponsePairListDto(questionResponsePairs);
    }
}

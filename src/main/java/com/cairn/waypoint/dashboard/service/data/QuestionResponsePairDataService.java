package com.cairn.waypoint.dashboard.service.data;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.SimplifiedHomeworkQuestionDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.QuestionResponsePairDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.QuestionResponsePairListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.SimplifiedHomeworkResponseDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionRepository;
import com.cairn.waypoint.dashboard.repository.HomeworkResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class QuestionResponsePairDataService {
    private final HomeworkResponseRepository homeworkResponseRepository;

    public QuestionResponsePairDataService( HomeworkQuestionDataService questionService,
                                            HomeworkResponseDataService homeworkResponseDataService,
                                            HomeworkResponseRepository homeworkResponseRepository,
                                            HomeworkQuestionRepository homeworkQuestionRepository
    ){
        this.homeworkResponseRepository = homeworkResponseRepository;
    }

    public QuestionResponsePairListDto findAllQuestionResponsePairsByUser(Long userId) {
        List<QuestionResponsePairDto> questionResponsePairDtos = homeworkResponseRepository.findAllByUserId(userId).stream()
                .map(response -> {
                    HomeworkQuestion question = response.getHomeworkQuestion();
                    if (question == null) {
                        throw new EntityNotFoundException("HomeworkQuestion not found for response ID: " + response.getId());
                    }
                    SimplifiedHomeworkQuestionDto sim_question = new SimplifiedHomeworkQuestionDto(question);
                    SimplifiedHomeworkResponseDto sim_response = new SimplifiedHomeworkResponseDto(response);
                    return new QuestionResponsePairDto(sim_question,sim_response);
                })
                .collect(Collectors.toList());

        return new QuestionResponsePairListDto(questionResponsePairDtos);
    }

}

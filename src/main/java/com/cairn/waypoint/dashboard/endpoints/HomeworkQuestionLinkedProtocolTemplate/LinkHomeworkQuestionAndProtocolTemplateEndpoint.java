package com.cairn.waypoint.dashboard.endpoints.HomeworkQuestionLinkedProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestionLinkedProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionRepository;
import com.cairn.waypoint.dashboard.repository.HomeworkQuestionLinkedProtocolTemplatesRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolTemplateRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/protocol-template-homework")
@Tag(name = "Protocol Template")
public class LinkHomeworkQuestionAndProtocolTemplateEndpoint {

    private final HomeworkQuestionLinkedProtocolTemplatesRepository linkedProtocolTemplateRepository;
    private final ProtocolTemplateRepository protocolTemplateRepository;
    private final HomeworkQuestionRepository homeworkQuestionRepository;

    public LinkHomeworkQuestionAndProtocolTemplateEndpoint(HomeworkQuestionLinkedProtocolTemplatesRepository linkedProtocolTemplateRepository,
                                                           ProtocolTemplateRepository protocolTemplateRepository,
                                                           HomeworkQuestionRepository homeworkQuestionRepository) {
        this.linkedProtocolTemplateRepository = linkedProtocolTemplateRepository;
        this.protocolTemplateRepository = protocolTemplateRepository;
        this.homeworkQuestionRepository = homeworkQuestionRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
    @Operation(
            summary = "Add a new HomeworkQuestionLinkedProtocolTemplate entry",
            description = "Allows a user to link a homework question to a protocol template.",
            security = @SecurityRequirement(name = "oAuth2JwtBearer"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created - Homework question linked to protocol template successfully",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = HomeworkQuestionLinkedProtocolTemplate.class))}),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = {@Content(schema = @Schema(hidden = true))}),
                    @ApiResponse(responseCode = "404", description = "Not Found",
                            content = {@Content(schema = @Schema(hidden = true))})
            }

    )
    public ResponseEntity<?> addHomeworkQuestionLinkedProtocolTemplate(@RequestBody AddHomeworkQuestionLinkedProtocolTemplateDto dto) {
        log.info("Linking homework question with ID [{}] to protocol template with ID [{}]",
                dto.getHomeworkQuestionId(), dto.getProtocolTemplateId());

        Optional<ProtocolTemplate> protocolTemplateOpt = protocolTemplateRepository.findById(dto.getProtocolTemplateId());
        if (protocolTemplateOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Protocol template with ID " + dto.getProtocolTemplateId() + " not found");
        }

        Optional<HomeworkQuestion> homeworkQuestionOpt = homeworkQuestionRepository.findById(dto.getHomeworkQuestionId());
        if (homeworkQuestionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Homework question with ID " + dto.getHomeworkQuestionId() + " not found");
        }

        HomeworkQuestionLinkedProtocolTemplate linkedEntry = HomeworkQuestionLinkedProtocolTemplate.builder()
                .protocolTemplate(protocolTemplateOpt.get())
                .question(homeworkQuestionOpt.get())
                .build();

        HomeworkQuestionLinkedProtocolTemplate savedEntry = linkedProtocolTemplateRepository.save(linkedEntry);

        HomeworkQuestionLinkedProtocolTemplateDto responseDto = HomeworkQuestionLinkedProtocolTemplateDto.builder()
                .id(savedEntry.getId())
                .protocolTemplateId(savedEntry.getProtocolTemplate().getId())
                .homeworkQuestionId(savedEntry.getQuestion().getId())
                .build();

        log.info("Successfully linked homework question with ID [{}] to protocol template with ID [{}]",
                dto.getHomeworkQuestionId(), dto.getProtocolTemplateId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}


package com.cairn.waypoint.dashboard.service.data;

import com.cairn.waypoint.dashboard.entity.ProtocolStepAttachment;
import com.cairn.waypoint.dashboard.repository.ProtocolStepAttachmentRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProtocolStepAttachmentDataService {

  private final ProtocolStepAttachmentRepository protocolStepAttachmentRepository;

  public ProtocolStepAttachmentDataService(
      ProtocolStepAttachmentRepository protocolStepAttachmentRepository) {
    this.protocolStepAttachmentRepository = protocolStepAttachmentRepository;
  }

  public Optional<ProtocolStepAttachment> getProtocolStepByFileGuid(String fileGuid) {
    return this.protocolStepAttachmentRepository.findByFileGuid(fileGuid);
  }
}

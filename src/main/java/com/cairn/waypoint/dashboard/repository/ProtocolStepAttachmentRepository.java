package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.ProtocolStepAttachment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProtocolStepAttachmentRepository extends
    JpaRepository<ProtocolStepAttachment, Long> {

  Optional<ProtocolStepAttachment> findByFileGuid(String fileGuid);
}

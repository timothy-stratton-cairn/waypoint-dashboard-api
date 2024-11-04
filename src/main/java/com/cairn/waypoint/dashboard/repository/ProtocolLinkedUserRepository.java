package com.cairn.waypoint.dashboard.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.cairn.waypoint.dashboard.entity.ProtocolLinkedUser;

public interface ProtocolLinkedUserRepository extends JpaRepository<ProtocolLinkedUser, Long> {
    Optional<ProtocolLinkedUser> findByUserId(Long userId);
    Optional<ProtocolLinkedUser> findByProtocol_Id(Long protocolId);
}

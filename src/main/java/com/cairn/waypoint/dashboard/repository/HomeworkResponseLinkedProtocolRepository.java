package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HomeworkResponseLinkedProtocol;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HomeworkResponseLinkedProtocolRepository extends JpaRepository<
    HomeworkResponseLinkedProtocol, Long> {

  List<HomeworkResponseLinkedProtocol> findByProtocol_Id(Long protocolId);


}
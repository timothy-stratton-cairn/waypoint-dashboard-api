package com.cairn.waypoint.dashboard.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.cairn.waypoint.dashboard.entity.HomeworkResponseLinkedProtocol;



public interface HomeworkResponseLinkedProtocolRepository extends JpaRepository< 
HomeworkResponseLinkedProtocol, Long> {

    List<HomeworkResponseLinkedProtocol> findByProtocol_Id(Long protocolId);
    
    
}
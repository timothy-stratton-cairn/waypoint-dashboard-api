package com.cairn.waypoint.dashboard;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Set;

@Slf4j
@SpringBootApplication
public class WaypointDashboardApiApp {

    @Autowired
    ProtocolRepository protocolRepository;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WaypointDashboardApiApp.class);
        app.setAdditionalProfiles("default");
        app.run(args);
        log.info("App is running...");
    }

    @PostConstruct
    public void init() {

        Protocol protocol1 = Protocol.builder()
                .name("protocol-1")
                .description("some description 1")
                .build();

        Protocol protocol2 = Protocol.builder()
                .name("protocol-2")
                .description("some description 2")
                .build();


        ProtocolUser protoUser1 = ProtocolUser.builder()
                .userId(1L)
                .protocolId(1L)
                .build();

        ProtocolUser protoUser2 = ProtocolUser.builder()
                .userId(2L)
                .protocolId(1L)
                .build();

        ProtocolUser protoUser3 = ProtocolUser.builder()
                .userId(3L)
                .protocolId(1L)
                .build();

        ProtocolUser protoUser4 = ProtocolUser.builder()
                .userId(4L)
                .protocolId(1L)
                .build();

        ProtocolUser protoUser5 = ProtocolUser.builder()
                .userId(1L)
                .protocolId(2L)
                .build();

        ProtocolUser protoUser6 = ProtocolUser.builder()
                .userId(5L)
                .protocolId(2L)
                .build();

        protocol1.setAssociatedUsers(Set.of(protoUser1, protoUser2, protoUser3, protoUser4));
        protocol2.setAssociatedUsers(Set.of(protoUser5, protoUser6));

        protocolRepository.saveAll(List.of(protocol1, protocol2));
    }
}

package com.cairn.waypoint.dashboard;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.repository.ProtocolRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolTemplateRepository;
import com.cairn.waypoint.dashboard.repository.ProtocolUserRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class WaypointDashboardApiApp {

  @Autowired
  ProtocolRepository protocolRepository;

  @Autowired
  ProtocolTemplateRepository protocolTemplateRepository;

  @Autowired
  ProtocolUserRepository protocolUserRepository;

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(WaypointDashboardApiApp.class);
    app.setAdditionalProfiles("default");
    app.run(args);
    log.info("App is running...");
  }

  @PostConstruct
  public void init() {

    ProtocolTemplate protocolTemplate1 = ProtocolTemplate.builder()
        .name("protocol-template-1")
        .description("some description 1")
        .modifiedBy("startup-testing")
        .build();

    ProtocolTemplate protocolTemplate2 = ProtocolTemplate.builder()
        .name("protocol-template-2")
        .description("some description 2")
        .modifiedBy("startup-testing")
        .build();
    protocolTemplate1 = protocolTemplateRepository.save(protocolTemplate1);
    protocolTemplate2 = protocolTemplateRepository.save(protocolTemplate2);

    Protocol protocol1 = Protocol.builder()
        .name("protocol-1")
        .description("some description 1")
        .protocolTemplate(protocolTemplate1)
        .modifiedBy("startup-testing")
        .build();

    Protocol protocol2 = Protocol.builder()
        .name("protocol-2")
        .description("some description 2")
        .protocolTemplate(protocolTemplate2)
        .modifiedBy("startup-testing")
        .build();
    protocol1 = protocolRepository.save(protocol1);
    protocol2 = protocolRepository.save(protocol2);

    ProtocolUser protoUser1 = ProtocolUser.builder()
        .userId(1L)
        .protocolId(protocol1.getId())
        .modifiedBy("startup-testing")
        .build();

    ProtocolUser protoUser2 = ProtocolUser.builder()
        .userId(2L)
        .protocolId(protocol1.getId())
        .modifiedBy("startup-testing")
        .build();

    ProtocolUser protoUser3 = ProtocolUser.builder()
        .userId(3L)
        .protocolId(protocol1.getId())
        .modifiedBy("startup-testing")
        .build();

    ProtocolUser protoUser4 = ProtocolUser.builder()
        .userId(4L)
        .protocolId(protocol1.getId())
        .modifiedBy("startup-testing")
        .build();

    ProtocolUser protoUser5 = ProtocolUser.builder()
        .userId(1L)
        .protocolId(protocol2.getId())
        .modifiedBy("startup-testing")
        .build();

    ProtocolUser protoUser6 = ProtocolUser.builder()
        .userId(5L)
        .protocolId(protocol2.getId())
        .modifiedBy("startup-testing")
        .build();

    protocol1.setAssociatedUsers(Set.of(protoUser1, protoUser2, protoUser3, protoUser4));
    protocol2.setAssociatedUsers(Set.of(protoUser5, protoUser6));

    protocolRepository.save(protocol1);
    protocolRepository.save(protocol2);
    protocolRepository.flush();
  }
}

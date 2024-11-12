package com.cairn.waypoint.dashboard;

import com.cairn.waypoint.dashboard.utility.protocoltriggering.StatusManager;
import com.cairn.waypoint.dashboard.utility.protocoltriggering.StatusObserver;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class WaypointDashboardApiApp {

  final List<StatusObserver> protocolStatusObservers;

  public WaypointDashboardApiApp(List<StatusObserver> protocolStatusObservers) {
    this.protocolStatusObservers = protocolStatusObservers;
  }

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(WaypointDashboardApiApp.class);
    app.setAdditionalProfiles("dev");
    app.run(args);
    log.info("App is running...");
  }

  @PostConstruct
  public void init() {
    for (StatusObserver observer : protocolStatusObservers) {
      StatusManager.subscribe(observer.getSubscribedRecurrenceType(), observer);
    }
  }
}

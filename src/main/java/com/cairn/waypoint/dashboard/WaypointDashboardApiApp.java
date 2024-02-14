package com.cairn.waypoint.dashboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class WaypointDashboardApiApp {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WaypointDashboardApiApp.class);
        app.setAdditionalProfiles("default");
        app.run(args);
        log.info("App is running...");
    }
}

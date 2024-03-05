FROM public.ecr.aws/docker/library/maven:3.8.5-openjdk-17-slim AS build
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package spring-boot:repackage

FROM public.ecr.aws/docker/library/maven:3.8.5-openjdk-17-slim
COPY --from=build /tmp/target/waypoint-dashboard-api.jar waypoint-dashboard-api.jar
EXPOSE 8083

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS

ENTRYPOINT [ "java","-jar", "waypoint-dashboard-api.jar" ]
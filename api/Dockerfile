FROM openjdk:21-jdk-slim
ADD api/build/libs/*.jar api.jar
ENTRYPOINT ["java","-jar","/api.jar"]
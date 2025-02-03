FROM openjdk:21-jdk-slim
ADD build/libs/*.jar api.jar
ENTRYPOINT ["java","-jar","/api.jar"]
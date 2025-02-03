FROM openjdk:21-jdk-slim
ADD crawler/build/libs/*.jar crawler.jar
ENTRYPOINT ["java","-jar","/crawler.jar"]
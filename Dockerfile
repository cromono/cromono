FROM openjdk:11.0.11-oraclelinux8
VOLUME /crontmp
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
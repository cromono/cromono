FROM openjdk:11.0.11-oraclelinux8
ARG JAR_FILE=build/libs/*.jar
VOLUME /crontmp
COPY JAR_FILE app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
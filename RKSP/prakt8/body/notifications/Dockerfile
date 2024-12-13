FROM gradle:latest as cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle /home/gradle/java-code/
WORKDIR /home/gradle/java-code
RUN gradle clean build -i --stacktrace

FROM gradle:latest as builder
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /usr/src/java-code/
WORKDIR /usr/src/java-code
RUN gradle bootJar -i --stacktrace

FROM openjdk:21

WORKDIR /usr/src/java-app
COPY --from=builder /usr/src/java-code/build/libs/notifications-0.0.1.jar /app/main.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/main.jar"]
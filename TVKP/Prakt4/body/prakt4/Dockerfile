FROM openjdk:17-alpine

WORKDIR /app

COPY pom.xml .
RUN apk add --no-cache maven && mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/prakt4-0.0.1-SNAPSHOT.jar"]
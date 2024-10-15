FROM eclipse-temurin:17-jdk-alpine

# WORKDIR /app

VOLUME /tmp

# COPY .mvn/ ./mvn
# COPY mvnw pom.xml ./

# RUN ./mvnw dependency:go-offline

# COPY src ./src

# CMD ["./mvnw", "spring-boot:run"]

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]



FROM openjdk:21-slim

WORKDIR /app

COPY target/sberlunch-1.0.0.jar /app

EXPOSE 3443

ENTRYPOINT ["java", "-jar", "sberlunch-1.0.0.jar"]
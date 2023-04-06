FROM openjdk:11
EXPOSE 443:443
RUN mkdir /app
COPY build/libs/*-all.jar /app/server.jar
ENTRYPOINT ["java", "-jar", "/app/server.jar"]
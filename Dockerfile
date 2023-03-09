FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
COPY build/libs/DigitalHotelServer.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
COPY build/libs/DigitalHotelServer.jar app.jar
COPY src/main/resources/keystore/key.p12 key.p12
ENTRYPOINT ["java", "-jar", "/app.jar"]

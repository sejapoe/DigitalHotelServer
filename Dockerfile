FROM openjdk:17
VOLUME /tmp
EXPOSE 443
COPY build/libs/DigitalHotelServer.jar app.jar
COPY src/main/resources/keystore/keystore.jks keystore.jks
ENTRYPOINT ["java", "-jar", "/app.jar"]

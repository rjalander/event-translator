FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
COPY cdevents-sdk-java-0.0.1.jar /app/lib/
ENTRYPOINT ["java","-cp","app:app/lib/*","com.ericsson.event.translator.EventTranslatorApplication"]

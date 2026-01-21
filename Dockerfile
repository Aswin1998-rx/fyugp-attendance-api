ARG JAVA_VERSION=17
ARG MAVEN_VERSION=3.9.4
ARG PROVIDER=eclipse-temurin

FROM maven:${MAVEN_VERSION}-${PROVIDER}-${JAVA_VERSION} AS builder
ENV APP_HOME /app
WORKDIR $APP_HOME
COPY .bin/HealthCheck.java .
RUN javac HealthCheck.java
COPY pom.xml pom.xml
RUN mvn -B dependency:resolve dependency:resolve-plugins
COPY lombok.config lombok.config
COPY src src
RUN mvn package -DskipTests

FROM ${PROVIDER}:${JAVA_VERSION}-jre-jammy
ENV APP_HOME /app
RUN addgroup --system java; \
    adduser --system --disabled-password --home $APP_HOME --ingroup java java
USER java
WORKDIR $APP_HOME
COPY --from=builder --chown=java:java $APP_HOME/HealthCheck.class .
HEALTHCHECK --interval=30s --timeout=10s --retries=2 CMD ["java", "HealthCheck", "||", "exit", "1"]
COPY --from=builder --chown=java:java $APP_HOME/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

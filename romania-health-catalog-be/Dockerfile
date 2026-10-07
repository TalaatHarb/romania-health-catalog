# syntax=docker/dockerfile:1

# the jar is platform independent, build once on the native platform for every target platform
FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:go-offline
COPY src src
# tests run in the pipeline before the image is built
RUN --mount=type=cache,target=/root/.m2 mvn -B -q package -DskipTests -Djacoco.skip=true \
    && cp target/romania-health-catalog-be-*.jar app.jar \
    && java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted

FROM eclipse-temurin:25-jre
RUN groupadd --system --gid 1001 app && useradd --system --uid 1001 --gid app --no-create-home app
WORKDIR /app
# layers from the least to the most frequently changing, so image updates mostly reuse the dependency layers
COPY --from=build /app/extracted/dependencies/ ./
COPY --from=build /app/extracted/spring-boot-loader/ ./
COPY --from=build /app/extracted/snapshot-dependencies/ ./
COPY --from=build /app/extracted/application/ ./
# the default (H2) profile stores its database in ./db, PostgreSQL is used with SPRING_PROFILES_ACTIVE=postgres
RUN mkdir db && chown app:app db
USER 1001:1001
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

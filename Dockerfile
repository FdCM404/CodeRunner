FROM gradle:9.1-jdk21 AS build
WORKDIR /src
COPY . .
RUN gradle :manager-server:shadowJar :run-server:shadowJar --no-daemon

FROM eclipse-temurin:21-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends docker.io \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /src/manager-server/build/libs/manager-server-1.0-all.jar /app/manager-server.jar
COPY --from=build /src/run-server/build/libs/run-server-1.0-all.jar /app/run-server.jar
ENTRYPOINT ["java"]

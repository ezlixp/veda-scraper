FROM alpine:3.23.5 AS downloads

RUN apk add --no-cache curl bash

WORKDIR /downloads

ENV HMC_VERSION=2.10.0

RUN curl -L "https://github.com/headlesshq/headlessmc/releases/download/${HMC_VERSION}/headlessmc-launcher-${HMC_VERSION}.jar" -o headlessmc-launcher.jar
RUN curl -L https://cdn.modrinth.com/data/P7dR8mSH/versions/BPX6fK06/fabric-api-0.97.3%2B1.20.4.jar -o fabric-api.jar


FROM gradle:8.10-jdk21-alpine AS builder

WORKDIR /mod

COPY gradle/ gradle/
COPY gradlew build.gradle* settings.gradle* gradle.properties* ./

RUN chmod +x gradlew

COPY src/ /mod/src/

RUN ./gradlew build --no-daemon --stacktrace

RUN rm -f build/libs/*-sources.jar build/libs/*-dev.jar build/libs/*-javadoc.jar && cp build/libs/*.jar /mod/mod.jar

FROM eclipse-temurin:17-jre-alpine AS final

RUN apk add --no-cache bash

WORKDIR /minecraft


RUN mkdir -p /minecraft/mods-cache
COPY --from=downloads /downloads/fabric-api.jar /minecraft/mods-cache
COPY --from=builder /mod/mod.jar /minecraft/mods-cache

COPY entrypoint.sh /minecraft/
RUN chmod +x entrypoint.sh

COPY --from=downloads /downloads/headlessmc-launcher.jar /minecraft
ENTRYPOINT ["/minecraft/entrypoint.sh"]
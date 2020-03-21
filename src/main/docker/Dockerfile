ARG JAVA_IMAGE=openjdk:13

FROM ${JAVA_IMAGE} as build

WORKDIR /src

COPY ./ /src

RUN yum -y install unzip

RUN ./gradlew clean build && \
    unzip build/libs/contact-service.jar -d /app

FROM ${JAVA_IMAGE}

COPY --from=build /app/BOOT-INF/lib /app/lib
COPY --from=build /app/BOOT-INF/classes /app
COPY --from=build /app/META-INF /app/META-INF
COPY src/main/script/startup.sh /app/startup.sh

EXPOSE 80

VOLUME /tmp

CMD ["/app/startup.sh"]

FROM openjdk:11 as build

WORKDIR /src

COPY ./ /src

RUN ./gradlew clean build

RUN unzip build/libs/contact-service-*.jar -d /app

FROM openjdk:11

COPY --from=build /app/BOOT-INF/lib /app/lib
COPY --from=build /app/BOOT-INF/classes /app
COPY --from=build /app/META-INF /app/META-INF
COPY src/main/script/startup.sh /app/startup.sh

EXPOSE 80

VOLUME /tmp

CMD ["/app/startup.sh"]

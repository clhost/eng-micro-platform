FROM bellsoft/liberica-runtime-container:jdk-all-17-slim-musl

ENV TZ=UTC

EXPOSE 8080

COPY ./build/libs/app.jar app.jar

CMD ["/usr/bin/java", "-jar", "app.jar"]

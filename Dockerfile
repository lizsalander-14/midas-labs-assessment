FROM openjdk:17
COPY ./out/production/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","MidasApplication"]
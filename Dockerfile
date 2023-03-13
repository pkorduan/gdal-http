FROM maven:3.8-jdk-11-openj9 as maven
LABEL maintainer="Ralf Trier GDI-Service"
LABEL version="0.2.2"

WORKDIR /app
COPY  src /app/src
RUN ls -al /app/src/*
COPY  pom.xml /app/
RUN mvn package
RUN ls -al /app/target/*
RUN ls -al /app/target/de.gdiservice.cmdserver-*.jar

FROM osgeo/gdal:ubuntu-full-3.2.2

RUN sed -i '/de_DE.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen
ENV LANG de_DE.UTF-8  
ENV LANGUAGE de_DE:de  
ENV LC_ALL de_DE.UTF-8

COPY --from=maven /app/target/de.gdiservice.cmdserver-*.jar /cmdserver/lib/main.jar
COPY --from=maven /app/target/alternateLocation /cmdserver/lib/
COPY cmd.properties /cmdserver/classes/

RUN ls -al /cmdserver/*
RUN ls -al /cmdserver/lib/main.jar
RUN unzip -l /cmdserver/lib/main.jar

WORKDIR /cmdserver

EXPOSE 8080

ENTRYPOINT [ "java", "-Djava.awt.headless=true", "-cp", "/cmdserver/classes/:/cmdserver/lib/main.jar", "de.gdiservice.cmdserver.CmdServer", "server"]

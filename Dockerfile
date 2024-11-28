FROM maven:3.8-jdk-11-openj9 as maven
LABEL maintainer="Ralf Trier GDI-Service"
LABEL version="0.6.0"

WORKDIR /app
COPY  src /app/src
RUN ls -al /app/src/*
COPY  pom.xml /app/
RUN mvn package
RUN ls -al /app/target/*
RUN ls -al /app/target/de.gdiservice.cmdserver-*.jar

# https://github.com/OSGeo/gdal/blob/master/docker/README.md
#FROM osgeo/gdal:ubuntu-small-3.2.2
#FROM ghcr.io/osgeo/gdal:ubuntu-small-3.8.5
FROM ghcr.io/osgeo/gdal:ubuntu-small-3.9.2

RUN apt-get update && apt-get install -y \
  default-jre \
  locales \
  apt-transport-https \
  ca-certificates && \
  update-ca-certificates

RUN localedef -i de_DE -c -f UTF-8 -A /usr/share/locale/locale.alias de_DE.UTF-8
ENV LANG de_DE.UTF-8  
ENV LANGUAGE de_DE:de  
ENV LC_ALL de_DE.UTF-8

COPY --from=maven /app/target/de.gdiservice.cmdserver-*.jar /cmdserver/lib/main.jar
COPY --from=maven /app/target/alternateLocation /cmdserver/lib/
COPY cmd.properties /cmdserver/classes/

RUN ls -al /cmdserver/*
RUN ls -al /cmdserver/lib/main.jar
RUN unzip -l /cmdserver/lib/main.jar

COPY sources/tippecanoe /usr/local/bin/tippecanoe
COPY sources/pmtiles /usr/local/bin/pmtiles

# Programm for float calculation on shell
RUN apt-get install -y bc

WORKDIR /cmdserver

EXPOSE 8080

ENTRYPOINT [ "java", "-Djava.awt.headless=true", "-cp", "/cmdserver/classes/:/cmdserver/lib/main.jar", "de.gdiservice.cmdserver.CmdServer", "server"]

FROM osgeo/gdal:ubuntu-full-3.2.2

LABEL maintainer="Ralf Trier GDI-Service"
LABEL version="0.1.0"

ADD /lib/* /cmdserver/lib/
ADD /classes/* /cmdserver/classes/
WORKDIR /cmdserver

EXPOSE 8080

ENTRYPOINT [ "java", "-Djava.awt.headless=true", "-cp", "/cmdserver/classes/:/cmdserver/lib/de.gdiservice.cmdserver-0.0.1.jar", "de.gdiservice.cmdserver.CmdServer", "server"]

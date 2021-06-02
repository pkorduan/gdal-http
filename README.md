# gdal-http
Docker Image of gdal Version 3.2.2 and a Service to execute ogrinfo, ogr2ogr and other via HTTP.

The file cmd.properties contains the toolName and the qualified path to the executable of the tool.

A call to the server looks like this:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`http://container:8080/t/?tool=toolName&param=''`

## Run the container ##

### You have to consider which resources the tool is using. ###

To accces a postgresql database:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The .pgpass has to be in the users home dir. The user within the docker container.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The host of the postgresql must be available within the container.

An exchange dir has to be set up if the tool needs access to files.

### Example ###

docker network create kvwmap_prod

docker run --name gdal-http -h gdalcmdserver -v /home/gisadmin/etc/postgresql/.pgpass:/root/.pgpass -v /home/gisadmin/www:/var/www/ pkorduan/gdal-http -d

docker network connect --alias pgsql kvwmap_prod pgsql-server

docker network connect --alias gdalcmdserver kvwmap_prod gdal-http

docker network connect --alias gdalclient kvwmap_prod gdalclient


`http://container:8080/t/?tool=ogr2ogr&param=-f "PostgreSQL" PG:"host='pgsql' port='5432' dbname='kvwmapsp' user='kvwmap' SCHEMAS=testschema_ralf" GMLAS:/var/www/tmp/temp.gml_2.gml -oo REMOVE_UNUSED_LAYERS=YES -oo XSD=/var/www/html/modell/xsd/5.1/XPlanung-Operationen.xsd`


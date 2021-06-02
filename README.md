# gdal-http
Docker Image mit gdal Version 3.2.2 und Service to execute ogrinfo and ogr2ogr via HTTP

## Run the container ##


docker create 

docker run ... --name pgsql-server ... --networkname --networkalias ... pkorduan/postgis:13.1-3.1

docker run --rm --name gdal-http -h gdalcmdserver --networkname --networkalias -v /home/gisadmin/etc/postgresql/.pgpass:/root/.pgpass -v /home/gisadmin/www:/var/www/ pkorduan/http -d

or start container mit dcm from kvwmap-server
dcm run gdal
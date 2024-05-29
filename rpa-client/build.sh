docker stop rpa-client
docker rm   rpa-client
docker rmi  rpa-client

docker build -t rpa-client .
docker run -d -it --name=rpa-client rpa-client

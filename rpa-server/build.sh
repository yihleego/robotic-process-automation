docker stop rpa-server
docker rm   rpa-server
docker rmi  rpa-server

docker build -t rpa-server .
docker run -d -it -p 11080:8080 -p 11888:18888 --name=rpa-server rpa-server
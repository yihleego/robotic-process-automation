FROM openjdk

COPY *.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["sh","-c","java -Dspring.profiles.active=prod -jar /app.jar"]
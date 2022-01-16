#define base docker image
FROM openjdk:17
LABEL maintener="springboot.cl"
ADD target/db-0.0.1-SNAPSHOT.jar db-healthit.jar
ENTRYPOINT ["java", "-jar", "db-healthit.jar"]
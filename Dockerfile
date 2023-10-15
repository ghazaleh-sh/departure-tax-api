FROM eclipse-temurin:17-jre-focal

VOLUME /tmp

ENV TZ=Asia/Tehran

RUN  mkdir -p /var/log/departure-tax-api
RUN  chmod -R 777 /var/log/departure-tax-api

COPY target/*.jar departure-tax-api-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-Xdebug","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:1520","-jar","/departure-tax-api-0.0.1-SNAPSHOT.jar"]
FROM eclipse-temurin:17.0.14_7-jdk-jammy
USER root
MAINTAINER sigsupport@rennesmetropole.fr
ENV TZ=Europe/Paris

RUN apt-get update && apt-get upgrade -y 
RUN mkdir /tmp/jetty
RUN chmod 777 -R /tmp && chmod o+t -R /tmp

RUN mkdir -p /etc/georchestra
RUN chmod 777 -R /etc/georchestra

RUN mkdir -p /opt/signalement
ADD georchestra-signalement-api.jar /opt/signalement/service.jar
RUN chmod 777 -R /opt/signalement

ENV PROPERTIES_PATH /etc/georchestra/signalement/signalement.properties

ADD entrypoint.sh /opt/signalement/entrypoint.sh
ENTRYPOINT [ "/bin/bash", "/opt/signalement/entrypoint.sh" ]

CMD [ "sh", "-c", "exec java --add-opens java.base/java.util=ALL-UNNAMED \
      -Duser.language=fr -Duser.country=FR \
      -Djava.io.tmpdir=/tmp/jetty \
      -Dgeorchestra.datadir=/etc/georchestra \
      -Dspring.config.additional-location=file:/etc/georchestra/ \
      ${ADD_JAVA_OPTS}                                   \
      -Xmx${XMX:-1G} -Xms${XMX:-1G}                      \
      -jar /opt/signalement/service.jar" ]  

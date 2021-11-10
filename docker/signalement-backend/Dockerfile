FROM jetty:9.4.31-jdk11
MAINTAINER tbrule@open-groupe.com
ENV TZ=Europe/Paris

ADD /var/lib/jetty/webapps/signalement.war /var/lib/jetty/webapps/signalement.war
#ADD etc/ /etc

# Jetty supplies Mail but not the activation dependency,
# if you use JavaMail it uses Jetty's JavaMail
# which then fails on Java >= 9 (even if we supply the
# activation dependency in Maven, as Jetty's JavaMail
# uses Jetty's classloader rather than ours)
RUN rm -r /usr/local/jetty/lib/mail

CMD ["sh", "-c", "exec java -Djava.io.tmpdir=/tmp/jetty \
  ${ADD_JAVA_OPTS} \
  -Dgeorchestra.datadir=/etc/georchestra \
  -Duser.language=fr -Duser.country=FR \
  -Xmx${XMX:-1G} -Xms${XMX:-1G} \
  -jar /usr/local/jetty/start.jar"]

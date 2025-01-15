FROM httpd:2.4.48-alpine
USER root
ENV TZ=Europe/Paris

ADD georchestra-signalement-front-management-angular-dist.zip /usr/local/apache2/htdocs/front.zip
ADD httpd.conf /usr/local/apache2/conf

RUN apk update && apk -s upgrade

RUN cd /usr/local/apache2/htdocs &&\
    unzip front.zip &&\
    rm front.zip &&\
    mv front-management signalement-admin

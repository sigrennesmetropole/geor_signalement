FROM httpd:2.4.48-alpine

ADD georchestra-signalement-front-management-angular-dist.zip /usr/local/apache2/htdocs/front.zip

RUN cd /usr/share/nginx/html &&\
    unzip front.zip &&\
    rm front.zip

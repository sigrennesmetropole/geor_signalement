FROM georchestra/mapfishapp

ADD signalement-0.0.1-SNAPSHOT-addon.zip /etc/georchestra/mapfishapp/addons/
RUN cd /etc/georchestra/mapfishapp/addons &&      \
    unzip signalement-0.0.1-SNAPSHOT-addon.zip &&   \
    rm signalement-0.0.1-SNAPSHOT-addon.zip
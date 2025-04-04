# Configuration *backend*

La configuration du back-office de trouve dans le fichier `signalement.properties`. Les principales propriétés sont :

```java
## Version de l'application
application.version=V0.0.1

# TEMPORARY DIRECTORY
temporary.directory=${java.io.tmpdir}/signalement

# LOG
logging.level.org.springframework=DEBUG
logging.level.org.georchestra=DEBUG

# SERVER 
server.port={{signalement_server_port}}
#spring.main.web-application-type=none

# BDD
spring.datasource.url=jdbc:postgresql://${pgsqlHost}:${pgsqlPort}/${pgsqlDatabase}?ApplicationName=signalement
spring.datasource.username={{signalement_db_user}}
spring.datasource.password={{signalement_db_password}}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect = org.hibernate.spatial.dialect.postgis.PostgisPGDialect
#spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults = false

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=false
# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto=none

# SECURITY
server.servlet.session.cookie.secure=true
#server.servlet.session.cookie.http-only=true
#server.servlet.session.tracking-modes=cookie

spring.security.user.name=admin
spring.security.user.password={noop}{{signalement_admin_password}}
spring.security.user.roles=USER

georchestra.role.administrator=ROLE_ADMINISTRATOR

# UPLOAD
# Taille maximum des fichiers à importer
spring.servlet.multipart.max-file-size=10MB

attachment.max-count=5
attachment.mime-types=image/png,image/jpeg,image/tiff,application/pdf,text/plain,text/html,text/csv,application/vnd.dxf,application/vnd.dwg,\
	application/vnd.oasis.opendocument.text,application/vnd.oasis.opendocument.spreadsheet,application/vnd.oasis.opendocument.presentation,application/vnd.oasis.opendocument.graphics,\
	application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-powerpoint,\
	application/vnd.openxmlformats-officedocument.presentationml.presentation,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document

# EMAIL
mail.transport.protocol=smtp
mail.smtp.host=${smtpHost}
mail.smtp.auth=false
mail.smtp.port=${smtpPort}
mail.smtp.user=
mail.smtp.password=
mail.smtp.starttls.enable=true
mail.debug=false
mail.from=${administratorEmail}

#LDAP
spring.ldap.urls=${ldapScheme}://${ldapHost}:${ldapPort}
spring.ldap.base=${ldapBaseDn}
spring.ldap.username=${ldapAdminDn}
spring.ldap.password=${ldapAdminPassword}

ldap.user.searchBase=${ldapUsersRdn}
ldap.objectClass=person
ldap.attribute.login=cn
ldap.attribute.firstName=givenname
ldap.attribute.lastName=sn
ldap.attribute.organization=description
ldap.attribute.email=mail

# GENERATION
freemarker.clearCache=false
freemarker.baseDirectory=${java.io.tmpdir}/models
freemarker.basePackage=models
freemarker.cssFile=
freemarker.fontsPath=fonts

## CONTEXT FRONT CONFIGURATION ##

#MAP FLOW
flow.url=https://public.sig.rennesmetropole.fr/geowebcache/service/wmts
flow.matrixSet=EPSG:3857
flow.version=1.0.0
flow.format=image/png
flow.projection=EPSG:3857
flow.layer=ref_fonds:pvci
flow.style=
flow.matrixId=EPSG:3857

#MAP START VIEW
view.zoom=11

#CENTER OF THE VIEW
view.x=-1.651
view.y=48.119

#COLOR HEXADECIMAL(RED,GREEN,BLUE,ALPHA/OPACITY)
color.fill=#2828284D
color.fill-hover=#565656B3
color.stroke=#28282888
color.stroke-hover=#282828FF

## END OF CONTEXT FRONT CONFIGURATION ##

```
# Addon-signalement

## I - Généralités

Le présent projet est destiné à permettre la gestion des signalements "spacialisés" et leur suivi au travers de workflow.

"Spacialisé" signifie ici que les signalements sont dessinés sous forme d'un polygone d'une ligne ou d'un point.

Les signalements peuvent être réalisés :

* Sur une couche : dans ce cas on rattache le signalement à un objet de la couche
* Sur un thème : dans ce cas le signalement n'est pas rattaché à une couche.

**/!\ Remarque :** un exemple de configuration d'un nouveau contexte est disponible [ici](readme/README_EXEMPLE.md)

Le présent projet met à disposition :

* Un **backend** ('signalement-api')
* Une **application back-office** ('front-management' mais ce composant est souvent appelé 'signalement-admin' ou 'signalement-bo')

    * Le manuel d'utilisation du back-office est accessible [ici](readme/README_BACKOFFICE.md)
    
* Un **plugin MapStore pour la déclaration des signalements** ('mapstore-addon')
* Un **plugin MapStore pour le suivi de ces déclarations** ('mapstore-management-addon')


## II - Construction de l'application

Le projet *git* est construit comme suit :

* `docker` : ce répertoire contient des propositions de fichiers Dockerfile pour la construction/modification des images dockers ainsi  qu'une proposition pour le fichier *docker-compose.yml*
* `georchestra-signalement-api` : il s'agit du sous-projet maven contenant l'application et les controleurs
* `georchestra-signalement-core` : il s'agit du sous-projet maven contenant les entités et les DAO
* `georchestra-signalement-service` : il s'agit du sous-projet maven contenant les services métiers, les services techniques
* `mapfish-addon` : il s'agit des sources de l'addon pour mapfishapp
* `mapstore-addon` :  il s'agit des sources de l'addon pour mapstore
* `readme` : les données nécessaires au présent document
* `resources` :  les resources avec notamment :
    * `sql` qui contient les fichiers SQL d'initialisation
    * `swagger` qui contient le fichier swagger permettant de générer l'ensemble des services REST du back-office
  
Le back-office est construit à partir de la commande maven

`mvn -DskipTest package`

Le résultat de cette construction est :
* Un fichier WAR `[projet]/georchestra-signalement-api/target/georchestra-signalement-api-1.0-SNAPSHOT.war` déployable directement dans Tomcat ou Jetty
* Un fichier SpringBoot JAR `[projet]/georchestra-signalement-api/target/georchestra-signalement-api-1.0-SNAPSHOT.jar`
* Un fichier `[projet]/georchestra-signalement-api/target/georchestra-signalement-api-1.0-SNAPSHOT-addon.zip`contenant l'addon MapfishApp
* Un fichier `[projet]/georchestra-signalement-api/target/georchestra-signalement-api-1.0-SNAPSHOT-extension.zip`contenant l'addon Mapstore

## III - Installation

L'addon signalement est conçu pour s'installer au sein d'une installation GeOrchestra existante mais la partie "backend" est indépendante de GeOrchestra.

### III.1 - Intégration *gitHub Rennes métropole*

Lors de la mise à jour du repository git `https://github.com/sigrennesmetropole/geor_signalement`, des actions Gits sont déclenchées afin :

- De builder les différents composants
- De pousser sur `https://hub.docker.com/r/sigrennesmetropole/geor_signalement` les images dockers du backend et de l'application back-office
- De déposer dans les artifacts les plugins MapStore sous forme de fichier zip téléchargeable

### III.1 - Base de données

#### III.1.a Initialisation

L'installation peut être réalisée soit :
* Dans une base de données dédiée
* Dans un schéma d'une base de données existantes

Dans tous les cas, il faut en premier lieu créer un utilisateur Postgres *signalement*.

```sql
CREATE USER signalement WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION;
```

Si l'installation est réalisée dans une base de données dédiée, il faut créer cette base :

```sql
CREATE DATABASE signalement
    WITH 
    OWNER = signalement
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;
```

Il faut ensuite exécuter le script `[projet]/resources/sql/signalement-initialisation.sql` en tant qu'administrateur postgres.

Ce script réalise les opérations suivantes :
* Création d'un schéma *signalement*
* Modification du user *signalement* afin de lui affecter un search_path à _signalement,public_
* Création des extensions geospatiales nécessaires dans le schéma _signalement_
* Création des tables, index, séquences dans le schéma

**Remarque 1**: seules les tables propres aux modules sont créées. Les tables propres à Activiti (le moteur de workflow) sont créées automatiquement au démarrage du service.

**Remarque 2**: il peut survenir au démarrage des erreurs "activity" (mention de l'absence de la colonne _version_ dans une table donnée par exemple). Ces erreurs proviennent en général des montées successives de schémas de la librairie. Il suffit donc de redémarrer l'application jusqu'à disparition de ces erreurs.

#### III.1.b Migration 1.3

Si un version inférieure à la 1.3 est déjà installée, il est nécessaire de jouer le script `[projet]/resources/sql/signalement-1.3.sql` afin de mettre à jour le schéma.

### III.2 - Déploiement de l'application *backend*

Le back-office peut être démarrer :

* Soit dans un container Tomcat 9.

Il suffit alors de déposer le fichier WAR produit dans le répertoire webapps de Tomcat.

* Soit dans un container Jetty

Il suffit alors de copier le fichier WAR produit dans le répertoire webapps de Jetty puis de lancer Jetty

```sh
cp signalement.war /var/lib/jetty/webapps/signalement.war
java -Djava.io.tmpdir=/tmp/jetty \
      -Dgeorchestra.datadir=/etc/georchestra 	\
      -Xmx${XMX:-1G} -Xms${XMX:-1G}           \
      -jar /usr/local/jetty/start.jar"
```

* Soit en lançant l'application SpringBoot à partir du JAR 

```
java -jar signalement.jar
```

**Remarque**: attention comme indiqué plus haut, l'application utilise la librairie "activity" qui créé ses propres tables au démarrage de l'application. Faut d'une configuration adéquate, ces tables peuvent atterrir dans le mauvais schéma. Il est donc important de dérouler le chapitre **III.1** avant toute chose.

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
spring.jpa.properties.hibernate.dialect = org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect
#spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults = false

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=false
# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto=update

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

### III.3 - Déploiement de l'addon MapfishApp

Le déploiement de l'addon MapfishApp est réalisé en dézippant le fichier `georchestra-signalement-api-1.0-SNAPSHOT-extension.zip` dans le répertoire `[georchestra]/config/mapfishapp/addons`.

Il faut ensuite modifier la propriété `signalementURL` présente dans fichier `manifest.json` afin de renseigner l'URL vers le back-office.

```json
	"default_options": {
		"signalementURL": "http://localhost:8082/"
	},
```

### III.4 - Déploiement des addons Mapstore

Le déploiement des plugins MapStore est réalisé en utilisant l'interface d'administration de MapStore permettant de créer des contextes en déposant les zip buildés.


## III - Configuration

### III.1 - Gestion des droits 

![Gestion des contextes et des droits](readme/UserRole.png)

La classe _User_ permet de gérer les utilisateurs potentiellement concernés par un signalement.
Les _User_s , identifiés par leur login, doivent aussi être présents dans le LDAP.

La classe _ContextDescription_ permet de lister les thèmes et les couches candidates pour un signalement.
Chaque contexte indique s'il s'agit d'un thème ou d'une couche, s'il s'agit d'une sélection par point, ligne ou polygone.
Chaque contexte est associé à un processus (et éventuellement une version de ce processus) qui sera utilisé lors de la création d'un signalement.

Un utilisateur peut être associé par le biais de la classe _UserRoleContext_ :
* A une liste de rôles, 
* A une liste de couples (rôle, context)
* A une liste de triplets (rôle, context, aire géographique)

### III.2 - Configuration des champs de formulaire d'une étape

![Gestion des formulaires](readme/FormDefinition.png)

Il est possible d'associer à une étape utilisateur un formulaire sous la forme d'un FormDefinition au moyen de la classe _ProcessFormDefinition_.
Si aucune version n'est précisée (_version = null_), toutes les versions sont impactées.

Chaque formulaire est associé à une liste de sections par le biais de la classe _FormSectionDefintion_.
Cette classe de liaison permet de définir :
* l'ordre d'affichage grâce aux champ order
* si la section est en lecture seule pour ce formulaire (par exemple pour afficher le commentaire d'une étape précédente non modifiable).

Chaque section possède (_SectionDefintion_):
* un nom unique, 
* un libellé,
* une définition sous le forme d'un flux json.

Le flux json est constitué comme suit:

```java
{
"fieldDefinitions": [
		{
		"name": <string>,
		"label": <string>,
		"type": ("STRING"|"BOOLEAN"|"LONG"|"DOUBLE"|"LIST"),
		"readOnly": false|true,
		"required": false|true,
		"multiple": false|true,
		"extendedType": "<flux json d'une liste d'objet avec code et label>",
		"validators": [
			{
				"type": ("MAXLENGTH"|"POSITIVE"|"NEGATIVE"),
				"attribute": ("<integer pour maxlength>"|null)
			}
		]
	},...
]
}
```

#### III.3 - Design des processus

### III.3.1 - Créer un processus dans la base

Le seul moyen de créer un nouveau processus ou une nouvelle version d'un processus est d'utiliser swagger et la méthode "admin" associée.

Pour uploader un fichier en ligne de commande :

```java
curl -v -X POST "http://localhost:8082/signalement/administration/processDefinition/update/simple" -H "accept: application/json" -H "authorization: Basic YWRtaW46NGRNMW5BcHAh" -H  "Content-Type: multipart/form-data" -F "file=@/tmp/simple.bpmn20.xml;type=application/xml"
```

Pour lister les processus déclarés :

```java
curl -X GET "http://georchestra.open-dev.com:8082/signalement/administration/processDefinition/search" -H  "accept: application/json" -H  "authorization: Basic YWRtaW46NGRNMW5BcHAh"
```

#### III.3.2 - Bien modéliser

Pour bien modéliser un processus, il est recommandé à chaque étape, de mettre à jour les données du signalement correspondant et notamment :
* son état
* la date de mise à jour
Des méthodes utilitaires sont disponibles pour cela (Cf. ci-dessous)

#### III.3.3 - Interactions des processus avec le signalement

La classe WorkflowContext propose un certain nombre de méthodes utilitaires :

* Les logs

```java
workflowContext.log(message);
```

* La mise à jour des données
Cette appel permet de modifier l'état du signalement et de mettre à jour la dernière date de modification.

```java
workflowContext.updateStatus(context, execution, "PENDING");
```
* Les mails

L'envoie de courriel commence par la création d'un objet EMailData.
Le construction d'EMailData prend 2 paramètres :
* Le sujet
* Le corps du courriel

Si le corps du courriel commence par `file:`, il est considéré comme un fichier et le template correspondant sera chargé.
Le chargement des templates se fait :
* Depuis le répertoire correspondant à la propriété `freemarker.baseDirectory`
* Depuis les resources de l'application dans un package défini par `freemarker.basePackage`

Dans le cas contraire, le contenu du paramètre sera considéré comme le template lui-même (il peut donc s'agir d'un texte inline).

```java
var localEmailData = new org.georchestra.signalement.core.dto.EMailData("Création d'un signalement", "file:initiator-mail.html");
```

Ensuite l'appel suivant envoie un courriel à l'initiateur du signalement avec le sujet et le corps contenu dans l'objet EMailData

```java
workflowContext.sendEMail(context,execution,localEmailData);
```

* L'assignation à une personne

Ce type d'assignation se fait en utilisant la balise `humanPerformer`

```xml
 <bpmn:humanPerformer id="HumanPerformer_1" name="Human Performer 1">
        <bpmn:resourceAssignmentExpression id="ResourceAssignmentExpression_3">
          <bpmn:formalExpression id="FormalExpression_3" evaluatesToTypeRef="ItemDefinition_1" language="http://www.java.com/java">
          	[...]
          </bpmn:formalExpression>
        </bpmn:resourceAssignmentExpression>
      </bpmn:humanPerformer>
```

L'expression formelle doit retourner un chaine de caractère correspond à un identifiant d'utilisateur.
L'objet `worfkflowContext` propose la méthode suivante : 

```java
${workflowContext.computeHumanPerformer(null,execution,"Validator", "Nouveau signalement", "file:assignee-mail.html")}
```

Les paramètres de la méthode sont :
* null (paramètre pour compabitilité)
* execution (paramètre implicite)
* Le nom du rôle correspondant
* Le sujet du courriel
* Le corps du courriel


* L'assignation à un groupe ou une liste de personnes

Ce type d'assignation se fait en utilisant la balise `potentialOwner`

```xml
      <bpmn:potentialOwner id="PotentialOwner_1" name="Potential Owner 1">
        <bpmn:resourceAssignmentExpression id="ResourceAssignmentExpression_4">
          <bpmn:formalExpression id="FormalExpression_8" language="http://www.java.com/java">
          	[...]
          </bpmn:formalExpression>
        </bpmn:resourceAssignmentExpression>
      </bpmn:potentialOwner>
```

L'expression formelle peut contenir une liste de valeurs séparées par des virgules.
Chaque valeur peut être de la forme `user(<expression>)` ou `group(<expression>)`.
dans le premier car, l'expression est un `candidateUser` dans le second l'expression est un `candidateGroup`. 

L'objet `worfkflowContext` propose la méthode suivante : 

```java
user(${workflowContext.computePotentialOwners(null,execution,"Validator", "Nouveau signalement", "file:assignee-mail.html")})
```
Les paramètres de la méthode sont :
* null (paramètre pour compabitilité)
* execution (paramètre implicite)
* Le nom du rôle correspondant
* Le sujet du courriel
* Le corps du courriel
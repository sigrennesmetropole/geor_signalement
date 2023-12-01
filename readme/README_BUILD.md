## Construction de l'application

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

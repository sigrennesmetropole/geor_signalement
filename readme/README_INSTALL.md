# Installation des composants

## Base de données

### Initialisation

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

### Migration 1.3

Si un version inférieure à la 1.3 est déjà installée, il est nécessaire de jouer le script `[projet]/resources/sql/signalement-1.3.sql` afin de mettre à jour le schéma.

## Déploiement de l'application *backend*

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

## Configuration du *backend*

[Configuration du backend](README_CONFIGURATION_BACKEND.md)

## Déploiement de l'addon MapfishApp

Le déploiement de l'addon MapfishApp est réalisé en dézippant le fichier `georchestra-signalement-api-1.0-SNAPSHOT-extension.zip` dans le répertoire `[georchestra]/config/mapfishapp/addons`.

Il faut ensuite modifier la propriété `signalementURL` présente dans fichier `manifest.json` afin de renseigner l'URL vers le back-office.

```json
	"default_options": {
		"signalementURL": "http://localhost:8082/"
	},
```

## Déploiement des addons Mapstore

Le déploiement des plugins MapStore est réalisé en utilisant l'interface d'administration de MapStore permettant de créer des contextes en déposant les zip buildés.

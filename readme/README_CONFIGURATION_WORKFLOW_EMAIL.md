# Configuration des courriels émis par les processus

Comme indiqué dans [Configuration Workflow](README_CONFGURATION_WORKFLOW.md), l'envoi de courriel par le processus nécessite la création d'un objet EmailData.

Cet objet est initialisé à partir d'un sujet et d'un template.
Le template est :
* soit un template inline (du code HTML brut)
* soit un fichier HTML dont le nom est préfixé par "file:"
* 
Le module de génération des courriels s'appuie sur [freemarker](https://freemarker.apache.org/).


La "Loader" de freemarker a été adapté pour fonctionner comme suit :

* le template est cherché dans le répertoire configurer par la *propriété* freemarker.baseDirectory
* si le template n'a pas été trouvé, il est cherché dans le le jar de l'application dans le chemin défini par la propriété *freemarker.basePackage*

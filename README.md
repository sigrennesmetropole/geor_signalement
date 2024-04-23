# Addon-signalement

## I - Généralités

Le présent projet est destiné à permettre la gestion des signalements "spacialisés" et leur suivi au travers de workflow.

"Spacialisé" signifie ici que les signalements sont dessinés sous forme d'un polygone d'une ligne ou d'un point.

Les signalements peuvent être réalisés :

* Sur une couche : dans ce cas on rattache le signalement à un objet de la couche
* Sur un thème : dans ce cas le signalement n'est pas rattaché à une couche.

**![](readme/warn.png)Remarque:** un exemple de configuration d'un nouveau contexte est disponible **[ici](readme/README_EXEMPLE.md)**

Le présent projet met à disposition :

* Un **backend** ('signalement-api')
* Une **application back-office** ('front-management' mais ce composant est souvent appelé 'signalement-admin' ou 'signalement-bo')

    * Le manuel d'utilisation du back-office est accessible **[ici](readme/README_BACKOFFICE.md)**
    
* Un **plugin MapStore pour la déclaration des signalements** ('mapstore-addon')
* Un **plugin MapStore pour le suivi de ces déclarations** ('mapstore-management-addon')


## II - Construction de l'application

[Construction de l'application](readme/README_BUILD.md)

## III - Installation

L'addon signalement est conçu pour s'installer au sein d'une installation GeOrchestra existante mais la partie "backend" est indépendante de GeOrchestra.

### III.1 - Intégration *gitHub Rennes métropole*

Lors de la mise à jour du repository git `https://github.com/sigrennesmetropole/geor_signalement`, des actions Gits sont déclenchées afin :

- De builder les différents composants
- De pousser sur `https://hub.docker.com/r/sigrennesmetropole/geor_signalement` les images dockers du backend et de l'application back-office
- De déposer dans les artifacts les plugins MapStore sous forme de fichier zip téléchargeable

### III.2 - Installation *à façon*

[Installation](readme/README_INSTALL.md)

### III.3 - Configuration *backend*

[Configuration backend](readme/README_CONFIGURATION_BACKEND.md)

## IV - Configuration

### IV.1 - Configuration des droits

[Configuration droits](readme/README_CONFIGURATION_DROIT.md)

### IV.2 - Configuration des champs de formulaire d'une étape

[Configuration formulaire](readme/README_CONFIGURATION_FORMULAIRE.md)

#### IV.3 - Conception des processus

[Conception des processus](readme/README_CONFIGURATION_WORKFLOW.md)

#### IV.4 - Configuration du certificat

[Configuration certificat](readme/README_CONFIGURATION_CERTIFICATE.md)

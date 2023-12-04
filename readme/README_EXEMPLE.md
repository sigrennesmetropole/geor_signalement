# Exemple de configuration d'un nouveau contexte

## Généralité

On considère ici un nouveau besoin de signalement sur une nouvelle thématique (il s'agit donc d'un contexte de type "thème" mais le processus est identique pour un contexte de typ "couche").

On considère également que le workflow de validation est assez proche de ceux existants mais l'on souhaite définir de nouveaux rôles pour les actions de validation et des templates de courriel différents.


<img src="warn.svg" alt="drawing" width="18px"/> 
**Remarques:** il faut considérer la création d'un nouveau contexte comme un projet à part entière. Cela signifie qu'il faut en premier lieu collecter le besion réel et réaliser une spécification détaillées décrivant l'attendu.

Cet attendu doit décrire notamment :

* Les étapes du workflow 
* Les formulaires attendus aux différentes étapes du workflow
* Le rôles à qui sont affectés les étapes
* Si des courriels sont émis aux différentes étapes, à qui ils sont émis ainsi que le corps et le sujet de chaque courriel.

Cet attendu pourra être ensuite traduit en actions de configuration.

## Création des rôles

Il s'agit ici de créer les différents rôles nécessaires au processus qui seront associés au contexte.

Ceci peut se faire grâce au back-office en suivant [back-office "Rôles création"](README_BACKOFFICE.md#role-creation)


## Préparer le processus

Il s'agit ici de modéliser le processus attendu pour le contexte.

Pour ce faire, ouvrir un processus existant dans un IDE de type eclipse muni du plugin "bpmn".
En suivant les règles décrites [ici](README_CONFIGURATION_WORKFLOW.md) modéliser le nouveau processus.

**/!\ Remarque :** n'oubliez pas de changer l'id dans le fichier XML représentant le procesuss afin de ne pas écraser un processus existant.

Il faut ensuite se rendre dans l'application back-office et téléverser le processus en suivant [back-office "Workflow"](README_BACKOFFICE.md#workflow-upload)

## Ajouter les formulaires liés au processus

Il s'agit ici de configurer les différents formulaires qui peuvent être affichés aux différentes étapes du processus.

Cette opération doit être réalisée directement en base de données (le back-office ne gère pas encore ces aspects).

Pour ce faire vous pouvez vous référer à [Configuration formulaire](README_CONFIGURATION_FORMULAIRE.md)

## Ajouter les templates de courriels 

Il s'agit ici de configurer les templates de courriels utilisés dans le processus tel qu'il a été modélisé.

Cette opération doit être réalisé en déposant les différents fichiers 

## Mettre à jour les droits

Il s'agit ici d'associer des utilisateurs à des rôles, des zones géographiques et un processus.

Ceci peut se faire grâce au back-office en suivant [back-office "Opérateurs création"](README_BACKOFFICE.md#operateur-creation)

## Création du contexte

Il s'agit ici de créer le contexte proprement dit. Cette création s'appuie sur les différents éléments configurés ou créés précédemment.

Ceci peut se faire grâce au back-office en suivant [back-office "Contexte création"](README_BACKOFFICE.md#contexte-creation)




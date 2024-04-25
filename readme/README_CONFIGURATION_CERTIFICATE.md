# Configuration du certificat

Un script est lancé au déploiement de l'image docker de l'application qui ajoute un certificat donné au keystore.  
Afin d'ajouter le bon certificat au bon keystore, il est nécessaire de remplir les informations adéquates dans le fichier `properties` : 

```
# filename du certificat (à déposer dans /etc/georchestra/signalement/)
server.trustcert.keystore.cert=
# nom de l'alias du certificat à insérer dans le keystore
server.trustcert.keystore.alias=
# chemin absolu du keystore dans le container docker
server.trustcert.keystore.store=
# mot de passe du keystore
server.trustcert.keystore.password=

```

Il est important de noter que la variable `signalement_server_keystore_cert` ne doit contenir que le _nom du fichier_, pas son chemin.  
Si les variables ne sont pas remplies, le certificat n'est pas ajouté au keystore et l'application démarre normalement.

Le certificat dont le nom est renseigné doit être déposé dans `/etc/georchestra/signalement/`.

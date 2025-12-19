# Configuration du certificat

Un script est lancé au déploiement de l'image docker de l'application afin de permettre d'ajouter un ou des certificats dans un ou des keystores.  

**Pour ajouter un seul certificat**

Afin d'ajouter le bon certificat au bon keystore, il est nécessaire de remplir les informations adéquates dans le fichier `properties` de l'application :

```yaml
# dossier contenant le certificat
server.trustcert.keystore.path=
# filename du certificat
server.trustcert.keystore.cert=
# nom de l'alias du certificat à insérer dans le keystore
server.trustcert.keystore.alias=
# chemin absolu du keystore dans le container docker
server.trustcert.keystore.store=
# mot de passe du keystore
server.trustcert.keystore.password=
```

Par exemple :

```
server.trustcert.keystore.path=/etc/georchestra/
server.trustcert.keystore.cert=signalement.crt
server.trustcert.keystore.alias=certificat-signalement
server.trustcert.keystore.store=/usr/local/openjdk-11/lib/security/cacerts
server.trustcert.keystore.password=changeit
```

Si les variables ne sont pas remplies, le certificat n'est pas ajouté au keystore et l'application démarre normalement.

**Pour ajouter plusieurs certificats**

Afin d'ajouter le bon certificat au bon keystore, il est nécessaire de remplir les informations adéquates dans le fichier `properties` de l'application :

```yaml
# propriété indiquant la liste des certificats à installer
server.trustcert.keystore.items=<item>,<item i>,<item n>
# pour chaque items lister ci-dessus le groupe des propriétés suivantes :
# dossier contenant le certificat pour l'item <item i>
server.trustcert.keystore.<item i>.path=
# filename du certificat pour l'item <item i>
server.trustcert.keystore.<item i>.cert=
# nom de l'alias du certificat à insérer dans le keystore pour l'item <item i>
server.trustcert.keystore.<item i>.alias=
# chemin absolu du keystore dans le container docker pour l'item <item i>
server.trustcert.keystore.<item i>.store=
# mot de passe du keystore pour l'item <item i>
server.trustcert.keystore.<item i>.password=
```

Par exemple :

```
server.trustcert.keystore.items=k8s,geo

server.trustcert.keystore.k8s.path=/etc/georchestra/
server.trustcert.keystore.k8s.cert=signalement.crt
server.trustcert.keystore.k8s.alias=certificat-signalement-k8s
server.trustcert.keystore.k8s.store=/usr/local/openjdk-11/lib/security/cacerts
server.trustcert.keystore.k8s.password=changeit

server.trustcert.keystore.geo.path=/etc/georchestra/
server.trustcert.keystore.geo.cert=signalement.crt
server.trustcert.keystore.geo.alias=certificat-signalement-geo
server.trustcert.keystore.geo.store=/usr/local/openjdk-11/lib/security/cacerts
server.trustcert.keystore.geo.password=changeit
```

Si pour un groupe (exemple : <item i>) de propriétés, il manque une propriété, le groupe est ignoré.
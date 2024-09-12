# Conception des processus

## Créer un processus dans la base

Il est possible de créer ou modifier un processus existant soit 

* En utilisant le *back-office* de signalement [ici](readme/README_BACKOFFICE.md)
* En utilisant swagger et la méthode "admin" associée.

Pour uploader un fichier en ligne de commande :

```java
curl -v -X POST "http://localhost:8082/signalement/administration/processDefinition/update/simple" -H "accept: application/json" -H "authorization: Basic YWRtaW46NGRNMW5BcHAh" -H  "Content-Type: multipart/form-data" -F "file=@/tmp/simple.bpmn20.xml;type=application/xml"
```

Pour lister les processus déclarés :

```java
curl -X GET "http://georchestra.open-dev.com:8082/signalement/administration/processDefinition/search" -H  "accept: application/json" -H  "authorization: Basic YWRtaW46NGRNMW5BcHAh"
```

*Attention :* votre fichier BPMN2.0 doit avoir l'extension *.bpmn20.xml*

## Bien modéliser

Pour bien modéliser un processus, il est recommandé à chaque étape, de mettre à jour les données du signalement correspondant et notamment :
* Son état
* La date de mise à jour
Des méthodes utilitaires sont disponibles pour cela (Cf. ci-dessous)

### Interactions des processus avec le signalement

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


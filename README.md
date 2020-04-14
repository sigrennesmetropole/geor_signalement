# addon-signalement

#### Gestion des droits 

![Gestion des contextes et des droits](readme/UserRole.png)

La classe User permet de gérer les utilisateurs potentiellement concernés par un signalement.
Les User , identifiés par leur login, doivent aussi être présents dans le LDAP.

La classe ContextDescription permet de lister les thèmes et les couches candidates pour un signalement.
Chaque contexte indique s'il s'agit d'un thème ou d'une couche, s'il s'agit d'une sélection par point, ligne ou polygon.
Chaque contexte est associé à un processus (et éventuellement une version de ce processus).

Un utilisateur peut être associé par le biais de la classe UserRoleContext :
* A une liste de rôles, 
* A une liste de couples (rôle, context)
* A une liste de triplets (rôle, context, aire géographic)



#### Design des processus

### Créer un processus dans la base

Le seul moyen de créer un nouveau processus ou une nouvelle version d'un processus est d'utiliser swagger et la méthode "admin" associée.

#### Bien modéliser

Pour bien modéliser un processus, il est recommandé à chaque étape, de mettre à jour les données du signalement correspondant et notamment :
* son état
* la date de mise à jour
Des méthodes utilitaires sont disponibles pour cela (Cf. ci-dessous)

#### Interactions des processus avec le signalement

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
${workflowContext.computeHumanPerformer(null,execution,&quot;Validator&quot;, &quot;Nouveau signalement&quot;, &quot;file:assignee-mail.html&quot;)}
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
user(${workflowContext.computePotentialOwners(null,execution,&quot;Validator&quot;, &quot;Nouveau signalement&quot;, &quot;file:assignee-mail.html&quot;)})
```
Les paramètres de la méthode sont :
* null (paramètre pour compabitilité)
* execution (paramètre implicite)
* Le nom du rôle correspondant
* Le sujet du courriel
* Le corps du courriel
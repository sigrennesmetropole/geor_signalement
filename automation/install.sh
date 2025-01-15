#!/bin/bash

set -x

POD_NAME=`kubectl get pods -o name | grep "signalement-back-${TARGET_ENV}" | sed -e 's:pod\/::g'`
echo "Current pod? $POD_NAME"

# ajout module
helm upgrade --install -f back.yaml -f config/${TARGET_ENV}/back.${TARGET_ENV}.yaml signalement-back-${TARGET_ENV} boost-stable/boost-deploy

# création du déploiement
kubectl apply -f config/${TARGET_ENV}/secret.${TARGET_ENV}.yaml

# ajout du répertoire partagé
sed -i "s/TARGET_ENV/${TARGET_ENV}/g" back-config.yaml

kubectl patch deployment signalement-back-${TARGET_ENV}-rm-signalement-back --patch-file back-config.yaml

kubectl apply -f config/${TARGET_ENV}/back.ingress.${TARGET_ENV}.yaml

helm upgrade --install -f front-management.yaml -f config/${TARGET_ENV}/front-management.${TARGET_ENV}.yaml signalement-front-management-${TARGET_ENV} boost-stable/boost-deploy

# on attend que tout soit démarré
sleep 15

# copie des fichiers
POD_NAME=`kubectl get pods -o name | grep "signalement-back-${TARGET_ENV}" | sed -e 's:pod\/::g'`
echo "Target pod $POD_NAME"
if [ ! -z "$POD_NAME" ]; then

	def_properties=`kubectl exec $POD_NAME -it -- ls /etc/georchestra/default.properties`
	if [ -z "${def_properties}" ]; then
		kubectl cp "config/${TARGET_ENV}/default.properties" $POD_NAME:"/etc/georchestra/default.properties"
	fi 
	kubectl cp "config/${TARGET_ENV}/log4j2.xml" $POD_NAME:"/etc/georchestra/signalement/log4j2.xml"
	kubectl cp "config/${TARGET_ENV}/signalement.properties" $POD_NAME:"/etc/georchestra/signalement/signalement.properties"
fi

# forcer le rédémarrage
if [ ! -z "${FORCE_PODS}" ]; then 
	kubectl delete pod -l app=signalement-back-${TARGET_ENV} ;
fi

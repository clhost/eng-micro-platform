#!/usr/bin/env bash

APP=$1
SHOULD_INCREASE_VERSION=$2

REGISTRY="localhost:5000"
CONFIG_FILE="$HOME/.engp/config"
VERSIONS_FILE="$HOME/.engp/src/versions.yml"
REPOSITORY_FOLDER=$(cat $CONFIG_FILE | grep "repositoryFolder:" | cut -d ' ' -f 2 | tr -d '"')

cd "$REPOSITORY_FOLDER/platform/$APP"

if [[ $SHOULD_INCREASE_VERSION == "true" ]]; then
  echo ""
  echo "====================================================================================="
  echo "Build Docker image for $APP"
  echo "====================================================================================="
  echo ""

  PREVIOUS_VERSION=$(cat $VERSIONS_FILE | grep "$APP:" | grep -oE "\d+")
  NEW_VERSION=$(($PREVIOUS_VERSION + 1))

  docker build --no-cache --progress=plain -t ${APP}:${NEW_VERSION} .
  docker tag ${APP}:${NEW_VERSION} ${REGISTRY}/${APP}:${NEW_VERSION}
  docker push ${REGISTRY}/${APP}:${NEW_VERSION}

  echo ""
  echo "Setting new version $NEW_VERSION..."

  if [[ $(uname) == "Darwin" ]]; then
    sed -i '' "s/${APP}:.*/${APP}: ${NEW_VERSION}/g" $VERSIONS_FILE
  fi

  if [[ $(uname) != "Darwin" ]]; then
    sed -i "s/${APP}:.*/${APP}: ${NEW_VERSION}/g" $VERSIONS_FILE
  fi
fi

echo ""
echo "====================================================================================="
echo "Deploy $APP to minikube"
echo "====================================================================================="
echo ""

cd "$REPOSITORY_FOLDER/platform/$APP/k8s/dev"

cp deployment-default.yml deployment-default-for-deploy.yml

VERSION=$(cat $VERSIONS_FILE | grep "$APP:" | grep -oE "\d+")
DEPLOYMENT_NAME=$(cat deployment-default-for-deploy.yml | yq -r '.metadata.name')

if [[ $(uname) == "Darwin" ]]; then
  sed -i '' "s/VERSION_PLACEHOLDER/$(echo $VERSION)/g" deployment-default-for-deploy.yml
fi

if [[ $(uname) != "Darwin" ]]; then
  sed -i "s/VERSION_PLACEHOLDER/$(echo $VERSION)/g" deployment-default-for-deploy.yml
fi

kubectl --context=minikube apply -f secrets-default.yml || true
kubectl --context=minikube apply -f configmap-default.yml
kubectl --context=minikube apply -f service-default.yml
kubectl --context=minikube apply -f deployment-default-for-deploy.yml

rm -f deployment-default-for-deploy.yml
kubectl --context=minikube rollout status deployment/$DEPLOYMENT_NAME --timeout=120s

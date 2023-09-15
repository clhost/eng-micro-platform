#!/usr/bin/env bash

CONFIG_FILE="$HOME/.engp/config"
VERSIONS_FILE="$HOME/.engp/src/versions.yml"

APP=$1
SHOULD_INCREASE_VERSION=$2
REPOSITORY_FOLDER=$(cat $CONFIG_FILE | grep "repositoryFolder:" | cut -d ' ' -f 2 | tr -d '"')
REGISTRY="localhost:5000"

./docker-check.sh
if [[ $? == "1" ]]; then
  exit 1
fi

./minikube-check.sh
if [[ $? == "1" ]]; then
  exit 1
fi

./build-and-deploy-app.sh $APP $SHOULD_INCREASE_VERSION

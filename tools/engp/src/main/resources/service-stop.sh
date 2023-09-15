#!/usr/bin/env bash

SERVICE=$1

./minikube-check.sh
if [[ $? == "1" ]]; then
  exit 1
fi

kubectl delete -f ../services/$SERVICE.yml

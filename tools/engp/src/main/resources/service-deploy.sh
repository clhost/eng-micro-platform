#!/usr/bin/env bash

SERVICE=$1

./minikube-check.sh
if [[ $? == "1" ]]; then
  exit 1
fi

kubectl --context=minikube apply -f ../services/$SERVICE.yml
kubectl --context=minikube rollout status sts/$SERVICE

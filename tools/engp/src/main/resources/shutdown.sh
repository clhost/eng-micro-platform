#!/usr/bin/env bash

./minikube-check.sh
if [[ $? == "1" ]]; then
  exit 1
fi

echo "Shutdown the environment..."
echo ""
minikube stop

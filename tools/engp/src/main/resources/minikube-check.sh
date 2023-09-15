#!/usr/bin/env bash

if ! command -v minikube > /dev/null; then
    echo "Not found."
    echo ""
    echo "====================================================================================="
    echo " Please install minikube on your system using your favourite package manager."
    echo ""
    echo " Restart after installing minikube."
    echo "====================================================================================="
    echo ""
    exit 1
fi

if ! command -v kubectl > /dev/null; then
    echo "Not found."
    echo ""
    echo "====================================================================================="
    echo " Please install kubectl on your system using your favourite package manager."
    echo ""
    echo " Restart after installing kubectl."
    echo "====================================================================================="
    echo ""
    exit 1
fi

MINIKUBE_STATUS=$(minikube status | grep "host:" | cut -d " " -f 2)

if [[ $MINIKUBE_STATUS == "Stopped" ]]; then
  echo "Minikube is not started. Please consider to start minikube by issuing 'engp startup'."
  exit 1
fi

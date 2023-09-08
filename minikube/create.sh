#!/usr/bin/env bash

echo "Looking for minikube..."
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

echo "Looking for docker..."
if ! command -v docker > /dev/null; then
    echo "Not found."
    echo ""
    echo "====================================================================================="
    echo " Please install docker on your system using your favourite package manager."
    echo ""
    echo " Restart after installing docker."
    echo "====================================================================================="
    echo ""
    exit 1
fi

if [[ $(uname) == "Darwin" ]]; then
  minikube start --insecure-registry "10.0.0.0/24" --vm-driver=hyperkit
fi

if [[ $(uname) != "Darwin" ]]; then
  minikube start --insecure-registry "10.0.0.0/24"
fi

echo ""
docker run -d --rm -it --network=host alpine ash -c "apk add socat && socat TCP-LISTEN:5000,reuseaddr,fork TCP:$(minikube ip):5000"
echo ""

echo ""
minikube dashboard --url &
echo ""

echo ""
minikube addons enable registry
echo ""

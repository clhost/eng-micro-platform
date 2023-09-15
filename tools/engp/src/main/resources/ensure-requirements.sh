#!/usr/bin/env bash

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
else
    echo "Found!"
    echo ""
fi

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
else
    echo "Found!"
    echo ""
fi

echo "Looking for kubectl..."
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
else
    echo "Found!"
    echo ""
fi

echo "Looking for psql..."
if ! command -v psql > /dev/null; then
    echo "Not found."
    echo ""
    echo "====================================================================================="
    echo " Please install psql on your system using your favourite package manager."
    echo ""
    echo " Restart after installing psql."
    echo "====================================================================================="
    echo ""
    exit 1
else
    echo "Found!"
    echo ""
fi

echo "Looking for redis-cli..."
if ! command -v redis-cli > /dev/null; then
    echo "Not found."
    echo ""
    echo "====================================================================================="
    echo " Please install redis-cli on your system using your favourite package manager."
    echo ""
    echo " Restart after installing redis-cli."
    echo "====================================================================================="
    echo ""
    exit 1
else
    echo "Found!"
    echo ""
fi

echo "Looking for yq..."
if ! command -v yq > /dev/null; then
    echo "Not found."
    echo ""
    echo "====================================================================================="
    echo " Please install yq on your system using your favourite package manager."
    echo ""
    echo " Restart after installing yq."
    echo "====================================================================================="
    echo ""
    exit 1
else
    echo "Found!"
    echo ""
fi

echo "Done!"

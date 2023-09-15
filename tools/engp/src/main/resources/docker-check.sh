#!/usr/bin/env bash

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

docker ps > /dev/null 2>&1

if [[ $? == "1" ]]; then
  echo "Docker daemon is not started. Please consider to start docker daemon."
  exit 1
fi

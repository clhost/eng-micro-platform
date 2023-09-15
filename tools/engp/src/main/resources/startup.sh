#!/usr/bin/env bash

APPLICATIONS="eng"
SHOULD_INCREASE_VERSION="true"

CONFIG_FILE="$HOME/.engp/config"
VERSIONS_FILE="$HOME/.engp/src/versions.yml"

REPOSITORY_FOLDER=$(cat $CONFIG_FILE | grep "repositoryFolder:" | cut -d ' ' -f 2 | tr -d '"')
REGISTRY="localhost:5000"

echo "Startup the environment..."

./docker-check.sh
if [[ $? == "1" ]]; then
  exit 1
fi

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

# 1. Work with minikube
echo ""
echo "====================================================================================="
echo "Startup minikube with insecure local registry hosted on $REGISTRY"
echo "====================================================================================="
echo ""

if [[ $(uname) == "Darwin" ]]; then
  minikube start --insecure-registry "10.0.0.0/24" --vm-driver=hyperkit
fi

if [[ $(uname) != "Darwin" ]]; then
  minikube start --insecure-registry "10.0.0.0/24"
fi

echo ""
echo "Starting dashboard..."
minikube dashboard --url & disown
echo ""

echo ""
minikube addons enable registry
echo ""

echo ""
if [[ -z $(docker ps | grep "eng-socat") ]]; then
  echo "Starting new socat..."
  docker run --name eng-socat -d --rm -it --network=host alpine ash -c "apk add socat && socat TCP-LISTEN:5000,reuseaddr,fork TCP:$(minikube ip):5000"
fi
echo ""

# 2. Work with infrastructure services
echo ""
echo "====================================================================================="
echo "Deploy infrastructure services to minikube"
echo "====================================================================================="
echo ""

echo ""
echo "====================================================================================="
echo "Deploy postgres"
echo "====================================================================================="
echo ""

kubectl --context=minikube apply -f services/postgres.yml
kubectl --context=minikube rollout status sts/postgres

HOST=$(minikube ip)
PORT=$(minikube service postgres --url --format={{.Port}})

echo ""
echo "Test connection from local machine..."
sleep 3
PGPASSWORD=pwd pg_isready -d postgres -h $HOST -p $PORT -U postgres
echo ""

echo "Test connection from inside minikube cluster..."
sleep 3
kubectl --context=minikube run pgbox --image=postgres:12 --rm -it --restart=Never \
        -- bash -c "PGPASSWORD=pwd pg_isready -d postgres -h ${HOST} -p ${PORT} -U postgres"
echo ""

for APP in $APPLICATIONS; do
  echo ""
  echo "Create database for $APP..."
  PGPASSWORD=pwd createdb -h $HOST -p $PORT -U postgres $APP
  echo "Done!"
  echo ""
done

echo ""
echo "====================================================================================="
echo "Deploy redis"
echo "====================================================================================="
echo ""

kubectl --context=minikube apply -f services/redis.yml
kubectl --context=minikube rollout status sts/redis

HOST=$(minikube ip)
PORT=$(minikube service redis --url --format={{.Port}})

echo ""
echo "Test connection from local machine..."
sleep 3
redis-cli -h $HOST -p $PORT ping
echo ""


# 3. Work with applications
for APP in $APPLICATIONS; do
  ./build-app-sources.sh $APP
  ./build-and-deploy-app.sh $APP $SHOULD_INCREASE_VERSION
done

echo ""
echo "Minikube status is"
minikube status

echo ""
echo "All done!"
exit 0

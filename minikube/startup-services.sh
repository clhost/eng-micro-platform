#!/usr/bin/env bash

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
fi

echo ""
echo "====================================================================================="
echo "Startup postgres"
echo "====================================================================================="
echo ""

kubectl --context=minikube apply -f services/postgres.yml
kubectl --context=minikube rollout status sts/postgres

HOST=$(minikube ip)
PORT=$(minikube service postgres --url --format={{.Port}})

echo ""
echo "Test connection from local machine..."
PGPASSWORD=pwd pg_isready -d postgres -h $HOST -p $PORT -U postgres
echo ""

echo "Test connection from inside minikube cluster..."
kubectl --context=minikube run pgbox --image=postgres:12 --rm -it --restart=Never \
        -- bash -c "PGPASSWORD=pwd pg_isready -d postgres -h ${HOST} -p ${PORT} -U postgres"
echo ""

echo ""
echo "Create eng database..."
PGPASSWORD=pwd createdb -h $HOST -p $PORT -U postgres eng
echo "Done!"
echo ""

echo ""
echo "====================================================================================="
echo "Startup redis"
echo "====================================================================================="
echo ""

kubectl --context=minikube apply -f services/redis.yml
kubectl --context=minikube rollout status sts/redis

HOST=$(minikube ip)
PORT=$(minikube service redis --url --format={{.Port}})

echo ""
echo "Test connection from local machine..."
redis-cli -h $HOST -p $PORT ping
echo ""

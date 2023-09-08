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
fi

echo ""
echo "====================================================================================="
echo "Build sources"
echo "====================================================================================="
echo ""

cd ..
./gradlew clean build -x ktlintMainSourceSetCheck -x ktlintTestSourceSetCheck

echo ""
echo "====================================================================================="
echo "Build Docker image for eng"
echo "====================================================================================="
echo ""

cd platform/eng

PREVIOUS_VERSION=$(cat ../../gradle.properties | grep "version" | grep -oE "\d+")
NEW_VERSION=$(($PREVIOUS_VERSION + 1))

docker build --no-cache --progress=plain -t eng:${NEW_VERSION} .
docker tag eng:${NEW_VERSION} localhost:5000/eng:${NEW_VERSION}
docker push localhost:5000/eng:${NEW_VERSION}

echo "Update version in gradle.properties"

if [[ $(uname) == "Darwin" ]]; then
  sed -i '' "s/version=0/version=${NEW_VERSION}/g" ../../gradle.properties
fi

if [[ $(uname) != "Darwin" ]]; then
  sed -i "s/version=0/version=${NEW_VERSION}/g" ../../gradle.properties
fi

echo ""
echo "====================================================================================="
echo "Deploy to minikube"
echo "====================================================================================="
echo ""

cd k8s/dev

cp deployment-default.yml deployment-default-for-deploy.yml

DEPLOYMENT_NAME=$(cat deployment-default-for-deploy.yml | yq -r '.metadata.name')

if [[ $(uname) == "Darwin" ]]; then
  sed -i '' "s/VERSION_PLACEHOLDER/$(echo $NEW_VERSION)/g" deployment-default-for-deploy.yml
fi

if [[ $(uname) != "Darwin" ]]; then
  sed -i "s/VERSION_PLACEHOLDER/$(echo $NEW_VERSION)/g" deployment-default-for-deploy.yml
fi

kubectl apply -f secrets-default.yml || true
kubectl apply -f configmap-default.yml
kubectl apply -f service-default.yml
kubectl apply -f deployment-default-for-deploy.yml

rm -f deployment-default-for-deploy.yml
kubectl rollout status deployment/$DEPLOYMENT_NAME --timeout=120s

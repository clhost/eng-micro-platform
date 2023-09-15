#!/usr/bin/env bash

CONFIG_FILE="$HOME/.engp/config"

APP=$1
REPOSITORY_FOLDER=$(cat $CONFIG_FILE | grep "repositoryFolder:" | cut -d ' ' -f 2 | tr -d '"')

echo ""
echo "====================================================================================="
echo "Build sources"
echo "====================================================================================="
echo ""

cd $REPOSITORY_FOLDER
./gradlew :$APP:clean :$APP:build -x ktlintMainSourceSetCheck -x ktlintTestSourceSetCheck -x test

#!/usr/bin/env bash
if [ -z "$1" ] ; then
  echo "Usage: ./cdk-run-tutorial.sh <path-to-tutorial>  E.g.: ./cdk-run-tutorial.sh ./1-networking/101-basic-vpc" && exit 1;
fi
if [ ! -d "$1" ]; then
  echo "Tutorial Path does not exist" && exit 1;
fi

TUTORIAL_DIR=$1
TUTORIAL_KEY=$(basename "$TUTORIAL_DIR")

source ./backend-env.conf
echo "Applying CDK Tutorial '$TUTORIAL_KEY' in $TUTORIAL_REGION region"
(cd cdk || exit; cdk deploy -f "workout-$TUTORIAL_KEY")

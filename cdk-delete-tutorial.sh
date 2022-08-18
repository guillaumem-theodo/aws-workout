#!/usr/bin/env bash
if [ -z "$1" ] ; then
  echo "Usage: ./cdk-delete-tutorial.sh <path-to-tutorial>" && exit 1;
fi
if [ ! -d "$1" ]; then
  echo "Tutorial Path does not exist" && exit 1;
fi

TUTORIAL_DIR=$1
TUTORIAL_KEY=$(basename "$TUTORIAL_DIR")

source ./backend-env.conf
echo "Deleting CDK Tutorial '$TUTORIAL_KEY' in $TUTORIAL_REGION region"
(cd cdk || exit; cdk destroy -f "workout-$TUTORIAL_KEY")

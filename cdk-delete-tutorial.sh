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
(cd cdk || exit; cdk destroy -f --require-approval never "workout-$TUTORIAL_KEY")

if [ -f "$1"/cdk_additional_delete.sh ]; then
  echo "Running additional commands";
  (cd "$1" || exit; ./cdk_additional_delete.sh)
fi

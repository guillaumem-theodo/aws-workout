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

if [ -f "$1"/cdk_additional_pre_run.sh ]; then
  echo "Running PRE-RUN commands for Tutorial '$TUTORIAL_KEY'";
  (cd "$1" || exit; ./cdk_additional_pre_run.sh)
fi

echo "Applying CDK Tutorial '$TUTORIAL_KEY' in $TUTORIAL_REGION region"
(cd cdk || exit; cdk deploy -f --require-approval never "workout-$TUTORIAL_KEY")

if [ -f "$1"/cdk_additional_post_run.sh ]; then
  echo "Running POST-RUN commands for Tutorial '$TUTORIAL_KEY'";
  (cd "$1" || exit; ./cdk_additional_post_run.sh)
fi

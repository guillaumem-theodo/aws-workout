#!/usr/bin/env bash
if [ -z "$1" ] ; then
  echo "Usage: ./delete-tutorial.sh <path-to-tutorial>" && exit 1;
fi
if [ ! -d "$1" ]; then
  echo "Tutorial Path does not exist" && exit 1;
fi

TUTORIAL_DIR=$1
TUTORIAL_KEY=$(basename "$TUTORIAL_DIR")

source ./backend-env.conf
echo "Deleting Tutorial '$TUTORIAL_KEY' Terraform in $TUTORIAL_REGION"
(cd "$1" || exit; terragrunt run-all --terragrunt-include-external-dependencies --terragrunt-non-interactive destroy -auto-approve)

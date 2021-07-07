#!/usr/bin/env bash
if [ -z "$1" ] ; then
  echo "Usage: ./run-tutorial.sh <path-to-tutorial>" && exit 1;
fi
if [ ! -d "$1" ]; then
  echo "Tutorial Path does not exist" && exit 1;
fi

TUTORIAL_DIR=$1
TUTORIAL_KEY=$(basename "$TUTORIAL_DIR")
TUTORIAL_DIR=$(dirname "$TUTORIAL_DIR")

## CHECK DEPENDENCIES
if [ -f "$1/dep.txt" ]; then
  cat "$1/dep.txt" | while read line
  do
   # Check if dependency has been properly applied
   # Check if the .terraform directory exists (in the dependency directory). Verify if terraform init was performed
    if [ ! -d "$TUTORIAL_DIR/$line/.terraform" ]; then
      echo "❌ Tutorial '$1' requires the tutorial >>>'$line'<<< to be applied first !!!" && exit 1;
      exit 1;
    fi
   # Check that there are at least one output in terraform state of the dependency
    found=$(cd "$TUTORIAL_DIR/$line"; terraform output -json | jq length)
    if [ "$found" -lt 1 ]; then
      echo "❌ Tutorial '$1' requires the tutorial >>>'$line'<<< to be applied first !!!" && exit 1;
    fi

  done || exit 1
  echo "✅ All dependencies have been properly found !!!"
fi

source ./backend-env.conf
TF_S3_REGION_VAR="tf-s3-region=$S3_BUCKET_REGION"
TF_S3_BUCKET_VAR="tf-s3-bucket=$S3_BUCKET"
TUTORIAL_REGION_VAR="region=$TUTORIAL_REGION"

echo "Applying Tutorial '$TUTORIAL_KEY' Terraform in $TUTORIAL_REGION_VAR"
(cd "$1" || exit; terraform apply -auto-approve -var "$TUTORIAL_REGION_VAR" -var "$TF_S3_REGION_VAR" -var "$TF_S3_BUCKET_VAR")

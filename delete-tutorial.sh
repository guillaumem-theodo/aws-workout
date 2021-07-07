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
TF_S3_REGION_VAR="tf-s3-region=$S3_BUCKET_REGION"
TF_S3_BUCKET_VAR="tf-s3-bucket=$S3_BUCKET"
TUTORIAL_REGION_VAR="region=$TUTORIAL_REGION"

echo "Deleting Tutorial '$TUTORIAL_KEY' Terraform in $TUTORIAL_REGION_VAR"
(cd "$1" || exit; terraform destroy -auto-approve -var "$TUTORIAL_REGION_VAR"  -var "$TF_S3_REGION_VAR" -var "$TF_S3_BUCKET_VAR")

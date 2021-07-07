#!/usr/bin/env bash
if [ -z "$1" ] ; then
  echo "Usage: ./init-tutorial.sh <path-to-tutorial>  E.g.: ./init-tutorial.sh ./1-networking/101-basic-vpc" && exit 1;
fi
if [ ! -d "$1" ]; then
  echo "Tutorial Path does not exist" && exit 1;
fi

TUTORIAL_DIR=$1
TUTORIAL_KEY=$(basename "$TUTORIAL_DIR")

source ./backend-env.conf
REGION_CMD="region=$S3_BUCKET_REGION"
S3_BUCKET_CMD="bucket=$S3_BUCKET"
S3_KEY_CMD="key=$TUTORIAL_KEY"
PROFILE_CMD="profile=aws-workout"

echo "Using Terraform backend in S3 bucket $S3_BUCKET_CMD located in $REGION_CMD"
(cd "$1" || exit; terraform init -backend-config="$REGION_CMD" -backend-config="$S3_BUCKET_CMD" -backend-config="$S3_KEY_CMD" -backend-config="$PROFILE_CMD")

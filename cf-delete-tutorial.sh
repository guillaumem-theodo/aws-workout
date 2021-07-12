#!/usr/bin/env bash
if [ -z "$1" ] ; then
  echo "Usage: ./delete-tutorial.sh <path-to-tutorial>" && exit 1;
fi
if [ ! -d "$1" ]; then
  echo "Tutorial Path does not exist" && exit 1;
fi
if [ ! -d "$1"/cf ]; then
  echo "Tutorial Path does not contain cf subdir" && exit 1;
fi

TUTORIAL_DIR=$1
TUTORIAL_KEY="stack-$(basename "$TUTORIAL_DIR")"

source ./backend-env.conf

echo "Deleting CloudFormation Tutorial '$TUTORIAL_KEY' in $TUTORIAL_REGION"
(cd "$1" || exit; aws cloudformation delete-stack --stack-name "$TUTORIAL_KEY" --region "$TUTORIAL_REGION" --profile aws-workout)

if [ -f "$1"/cf/stack_additional.yaml ]; then
  echo "Deleting an additional stack in another region ($TUTORIAL_ANOTHER_REGION)";
  (cd "$1"/cf || exit; aws cloudformation delete-stack --stack-name "$TUTORIAL_KEY-additional" --region "$TUTORIAL_ANOTHER_REGION" --profile aws-workout)
fi

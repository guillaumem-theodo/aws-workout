#!/usr/bin/env bash
if [ -z "$1" ] ; then
  echo "Usage: ./run-cf-tutorial.sh <path-to-tutorial>" && exit 1;
fi
if [ ! -d "$1" ]; then
  echo "Tutorial Path does not exist" && exit 1;
fi
if [ ! -d "$1"/cf ]; then
  echo "Tutorial Path does not contain 'cf' subdir" && exit 1;
fi


TUTORIAL_DIR=$1
TUTORIAL_KEY="stack-$(basename "$TUTORIAL_DIR")"

source ./backend-env.conf

## CHECK DEPENDENCIES
if [ -f "$1"/dep.txt ]; then
  cat "$1"/dep.txt | while read line
  do
   # Check if dependency has been properly applied
   # Check if the cloudformation stack of the dependency has been applied successfully
    stackname=$(basename "$line")
    found=$(aws cloudformation list-stacks --region "$TUTORIAL_REGION" --profile aws-workout --query "StackSummaries[?StackName == 'stack-$stackname'].StackName" --stack-status-filter CREATE_COMPLETE --output text)
    if [ -z "$found" ]; then
      echo "❌ Tutorial '$1' requires the tutorial >>>'$line'<<< to be applied first !!!" && exit 1;
    fi

  done || exit 1
  echo "✅ All dependencies have been properly found !!!"
fi

echo "Applying CloudFormation Tutorial '$TUTORIAL_KEY' in $TUTORIAL_REGION"
(cd "$1"/cf || exit; aws cloudformation create-stack --stack-name "$TUTORIAL_KEY" --region "$TUTORIAL_REGION" --profile aws-workout --template-body file://./stack.yaml)

if [ -f "$1"/cf/stack_additional.yaml ]; then
  echo "Adding an additional stack in another region ($TUTORIAL_ANOTHER_REGION)";
  (cd "$1"/cf || exit; aws cloudformation create-stack --stack-name "$TUTORIAL_KEY-additional" --region "$TUTORIAL_ANOTHER_REGION" --profile aws-workout --template-body file://./stack_additional.yaml)
fi

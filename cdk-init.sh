#!/usr/bin/env bash
account_id=$(aws sts get-caller-identity --profile aws-workout --query 'Account' --output text)

source ./backend-env.conf
echo "Bootstrapping CDK for Workouts in $TUTORIAL_REGION region"
cdk bootstrap aws://$account_id/$TUTORIAL_REGION
echo "Bootstrapping CDK for Workouts in $TUTORIAL_ANOTHER_REGION region"
cdk bootstrap aws://$account_id/$TUTORIAL_ANOTHER_REGION

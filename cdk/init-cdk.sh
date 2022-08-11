#!/usr/bin/env bash
account_id=$(aws sts get-caller-identity --profile aws-workout --query 'Account' --output text)

source ../backend-env.conf
echo "Initialize CDK in $TUTORIAL_REGION"
cdk bootstrap aws://$account_id/$TUTORIAL_REGION
echo "Initialize CDK in $TUTORIAL_ANOTHER_REGION"
cdk bootstrap aws://$account_id/$TUTORIAL_ANOTHER_REGION

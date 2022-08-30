#!/usr/bin/env bash

bucket_1=$(aws cloudformation describe-stacks --region "$TUTORIAL_REGION" --profile aws-workout --stack-name workout-208-sls-lambda --query "Stacks[0].Outputs[?OutputKey=='cpu208bucket1'].OutputValue" --output text)
bucket_2=$(aws cloudformation describe-stacks --region "$TUTORIAL_REGION" --profile aws-workout --stack-name workout-208-sls-lambda --query "Stacks[0].Outputs[?OutputKey=='cpu208bucket2'].OutputValue" --output text)

if [ -z "$bucket_1" ] ; then
  bucket_1="any_name"
fi
if [ -z "$bucket_2" ] ; then
  bucket_2="any_name"
fi

(cd sls; yarn; BUCKET_NAME=$bucket_1 AWS_PROFILE=aws-workout serverless remove)


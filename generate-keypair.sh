#!/usr/bin/env bash

## GENERATE A KEY PAIR for SSH in future EC2 instances
rm -f aws-workout-key-pair*
ssh-keygen -P '' -q -m PEM -f aws-workout-key-pair
mv aws-workout-key-pair aws-workout-key-pair.pem
chmod 400 aws-workout-key-pair.pem

source ./backend-env.conf

## IMPORT THE PUBLIC KEY of the KEY PAIR in AWS for EC2
aws ec2 delete-key-pair --key-name aws-workout-key --region "$TUTORIAL_REGION" --profile aws-workout
aws ec2 import-key-pair --key-name aws-workout-key --public-key-material fileb://./aws-workout-key-pair.pub --region "$TUTORIAL_REGION" --profile aws-workout

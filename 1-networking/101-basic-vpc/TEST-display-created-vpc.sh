#!/usr/bin/env bash
## DISPLAY created VPC
echo "✅ Display the created VPC"
aws ec2 describe-vpcs --region "$TUTORIAL_REGION" --filters Name=tag:Name,Values=net-101-vpc --profile aws-workout

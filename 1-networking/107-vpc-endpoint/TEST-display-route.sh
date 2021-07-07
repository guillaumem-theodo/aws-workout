#!/usr/bin/env bash
## RETRIEVE the VPC_ID created in 101-basic-vpc step
vpc_id=$(aws ec2 describe-vpcs --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values=net-101-vpc --query 'Vpcs[0].VpcId' --output text)

## DISPLAY the Routes created in dojo
echo "✅ Displaying route table created in Tutorial 1O4 for VPC $vpc_id. Should contain a route to S3 through Endpoint"
aws ec2 describe-route-tables --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=vpc-id,Values="$vpc_id" Name=tag:Name,Values=net-105-rt-2

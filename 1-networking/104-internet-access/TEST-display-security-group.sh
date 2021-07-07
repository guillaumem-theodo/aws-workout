#!/usr/bin/env bash
## RETRIEVE the VPC_ID created in 101-basic-vpc step
vpc_id=$(aws ec2 describe-vpcs --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values=net-101-vpc --query 'Vpcs[0].VpcId' --output text)

## DISPLAY the SecurityGroup created in the tutorial 104
echo "✅ Displaying security group created in Tutorial 1O4 for VPC $vpc_id"
aws ec2 describe-security-groups --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=vpc-id,Values="$vpc_id" Name=tag:Name,Values=net-104-sg

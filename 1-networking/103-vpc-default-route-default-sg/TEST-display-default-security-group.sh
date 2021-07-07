#!/usr/bin/env bash
## RETRIEVE the VPC_ID created in 101-basic-vpc step
vpc_id=$(aws ec2 describe-vpcs --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values=net-101-vpc --query 'Vpcs[0].VpcId' --output text)

## DISPLAY the rules of the DEFAULT SecurityGroup associated by default with the VPC
echo "✅ Displaying default security group created in Tutorial 1O1 for VPC $vpc_id"
aws ec2 describe-security-groups --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=vpc-id,Values="$vpc_id" --query 'SecurityGroups[?GroupName == `default`]'

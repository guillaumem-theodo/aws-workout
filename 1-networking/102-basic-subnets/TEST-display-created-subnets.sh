#!/usr/bin/env bash
## RETRIEVE the VPC_ID created in 101-basic-vpc step
vpc_id=$(aws ec2 describe-vpcs --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values=net-101-vpc --query 'Vpcs[0].VpcId' --output text)

## DISPLAY created Subnets
echo "✅ Display created Subnets for the VPC $vpc_id : you should see 4 subnets spanning on 2 AvailabilityZones in the region"
aws ec2 describe-subnets --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=vpc-id,Values="$vpc_id" --query 'Subnets[].[SubnetId, CidrBlock, AvailabilityZone]'

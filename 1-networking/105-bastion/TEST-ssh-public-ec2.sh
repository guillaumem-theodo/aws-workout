#!/usr/bin/env bash

## TRY TO SSH on EC2 --> SHOULD BE OK
ec2_bastion_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-105-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
echo "✅ Trying to SSH into the BASTION EC2 created in Tutorial 105 with public IP: $ec2_bastion_public_ip"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 ec2-user@"$ec2_bastion_public_ip"

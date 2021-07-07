#!/usr/bin/env bash

## TRY TO SSH on EC2 --> SHOULD BE OK
ec2_3_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-3"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
ec2_2_private_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-2"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)

echo "✅ Trying to SSH into the private EC2 created in Tutorial 105 with private IP: $ec2_2_private_ip through EC2: $ec2_3_public_ip"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -J ec2-user@"$ec2_3_public_ip" ec2-user@"$ec2_2_private_ip"

#!/usr/bin/env bash

## TRY TO GET IPs of all EC2
ec2_3_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-3" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
ec2_1_private_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-1" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)
ec2_2_private_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-2" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)
ec2_3_private_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-3" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)

echo "✅ Test Connection from EC2 in VPC 3 ($ec2_3_public_ip) TO EC2 in VPC 2 ($ec2_2_private_ip)"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@"$ec2_3_public_ip" ping -c 2 "$ec2_2_private_ip"

echo "✅ Test Connection from EC2 in VPC 2 ($ec2_2_private_ip using forwarding agent) TO EC2 in VPC 3 ($ec2_3_private_ip)"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no -J ec2-user@"$ec2_3_public_ip" ec2-user@"$ec2_2_private_ip" ping -c 2 "$ec2_3_private_ip"

echo "✅ Test Connection from EC2 in VPC 2 ($ec2_2_private_ip using forwarding agent) TO EC2 in VPC 1 ($ec2_1_private_ip)"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no -J ec2-user@"$ec2_3_public_ip" ec2-user@"$ec2_2_private_ip" ping -c 2 "$ec2_1_private_ip"

echo "❌ VPC Peering is not transitive ($ec2_3_public_ip) CAN'T REACH EC2 in VPC 1 ($ec2_1_private_ip)"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@"$ec2_3_public_ip" ping -c 2 "$ec2_1_private_ip"

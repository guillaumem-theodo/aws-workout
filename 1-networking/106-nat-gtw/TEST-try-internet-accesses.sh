#!/usr/bin/env bash

## TRY TO SSH on EC2 --> SHOULD BE OK
ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-105-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
ec2_2_private_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-105-ec2-2"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)

echo "✅ Trying to reach internet from the BASTION EC2 created in Tutorial 105 with public IP: $ec2_1_public_ip through 👉 InternetGateway"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@"$ec2_1_public_ip" curl -m 10 -r 0-200 https://pokeapi.co/api/v2/pokemon/pikachu

echo "✅ Trying to reach internet (should be OK) from the private EC2 created in Tutorial 105 with private IP: $ec2_2_private_ip through 👉 NAT Gateway"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no -J ec2-user@"$ec2_1_public_ip" ec2-user@"$ec2_2_private_ip" curl -m 10 -r 0-200 https://pokeapi.co/api/v2/pokemon/pikachu

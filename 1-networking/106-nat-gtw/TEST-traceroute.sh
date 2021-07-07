#!/usr/bin/env bash

## TRY TO SSH on EC2 --> SHOULD BE OK
ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-105-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
ec2_2_private_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-105-ec2-2"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)
nat_gtw_private_ip=$(aws ec2 describe-addresses --region "$TUTORIAL_REGION" --profile aws-workout --filters Name=tag:Name,Values="net-106-nat-gtw-eip" --query 'Addresses[0].PrivateIpAddress' --output text)

echo "✅ Traceroute internet through InternetGateway (from Bastion EC2)"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 ec2-user@"$ec2_1_public_ip" traceroute -m 2 pokeapi.co

echo "✅ Traceroute internet through NAT Gateway (from Private EC2) - first HOP is 👉 NAT gateway private ip:$nat_gtw_private_ip"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -J ec2-user@"$ec2_1_public_ip" ec2-user@"$ec2_2_private_ip" traceroute -m 2 pokeapi.co

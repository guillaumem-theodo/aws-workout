#!/usr/bin/env bash

ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="cpu-201-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
echo "✅ Ping the public EC2 created in Tutorial 201 with public IP: $ec2_1_public_ip"
ping -c 2 "$ec2_1_public_ip"

echo "✅ Test Port 80 EC2 created in Tutorial 201 with public IP: $ec2_1_public_ip"
nc -vz "$ec2_1_public_ip" 80

echo "❌ Test Port 443 EC2 created in Tutorial 201 with public IP: $ec2_1_public_ip SHOULD TIMEOUT in 10s"
nc -v -z -w 10 -G 10 "$ec2_1_public_ip" 443

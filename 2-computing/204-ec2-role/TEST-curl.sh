#!/usr/bin/env bash

ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="cpu-204-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
echo "✅ Trying to CURL site with public IP: $ec2_1_public_ip"
curl "http://$ec2_1_public_ip:80"

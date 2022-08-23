#!/usr/bin/env bash

## TRY TO SEE IF HTTPD SERVICE is RUNNING
ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="cpu-202-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
echo "✅ Trying to check HTTPD Service: $ec2_1_public_ip"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@"$ec2_1_public_ip" sudo service httpd status

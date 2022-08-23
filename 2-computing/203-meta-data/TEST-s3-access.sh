#!/usr/bin/env bash

ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="cpu-203-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
echo "❌ Trying to access S3 bucket from with EC2: $ec2_1_public_ip - should be KO"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@"$ec2_1_public_ip" aws s3 ls s3://unique-name-s3-bucket-1-203

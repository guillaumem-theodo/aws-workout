#!/usr/bin/env bash
## RETRIEVE the IPs
ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-105-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
ec2_2_private_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-105-ec2-2"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)

## DISPLAY the Routes to S3
echo "✅ S3 ls in region '$TUTORIAL_REGION' -> Success. It goes through VPC endpoint"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -J ec2-user@"$ec2_1_public_ip" ec2-user@"$ec2_2_private_ip" aws s3 ls --region=$TUTORIAL_REGION s3://unique-name-s3-bucket-1-107

echo "❌ S3 ls in another region '$TUTORIAL_ANOTHER_REGION' -> Should fail (timeout). Since there is no route to internet and no NAT Gateway"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -J ec2-user@"$ec2_1_public_ip" ec2-user@"$ec2_2_private_ip" aws --cli-read-timeout=3 --cli-connect-timeout=3 s3 ls --region=$TUTORIAL_ANOTHER_REGION  s3://unique-name-s3-bucket-2-107

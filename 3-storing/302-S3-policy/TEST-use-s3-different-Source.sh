#!/usr/bin/env bash

ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="sto-302-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)


echo "❌ Get/Retrieve an object from S3 from my IP"
aws s3 cp --region "$TUTORIAL_REGION"  --profile aws-workout s3://unique-name-s3-bucket-3-301/my-key-3 ./tmp.txt

echo "✅ Get/Retrieve an object from S3 from VPC through VPC Endpoint"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 ec2-user@"$ec2_1_public_ip" aws s3 cp s3://unique-name-s3-bucket-3-301/my-key-3 ./tmp.txt


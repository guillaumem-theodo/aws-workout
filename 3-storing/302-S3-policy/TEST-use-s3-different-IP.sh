#!/usr/bin/env bash

ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="sto-302-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
ec2_2_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="sto-302-ec2-2"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)


## Navigate in S3 buckets
echo "✅ List all objects in a bucket matching a name (unique-name-s3-bucket-2-301)"
aws s3 ls --region "$TUTORIAL_REGION"  --profile aws-workout s3://unique-name-s3-bucket-2-301

echo "✅ Get/Retrieve an object from S3 from my IP"
aws s3 cp --region "$TUTORIAL_REGION"  --profile aws-workout s3://unique-name-s3-bucket-2-301/my-key-3 ./tmp.txt

echo "✅ Get/Retrieve an object from S3 from authorized IP(EC2 1)"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 ec2-user@"$ec2_1_public_ip" aws s3 cp s3://unique-name-s3-bucket-2-301/my-key-3 ./tmp.txt

echo "❌ Cant Get/Retrieve an object from S3 from non-authorized IP(EC2 2)"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 ec2-user@"$ec2_2_public_ip" aws s3 cp s3://unique-name-s3-bucket-2-301/my-key-3 ./tmp.txt

#!/usr/bin/env bash

ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="cpu-208-ec2-test-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
echo "✅ Test S3 ls from within VPC - EC2 public IP: $ec2_1_public_ip"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@"$ec2_1_public_ip" aws s3 ls --region eu-west-1  "s3://$TUTORIAL_UNIQUE_KEY-s3-bucket-1"

echo "✅ Test S3 ls from local laptop"
aws s3 ls --region eu-west-1  --profile aws-workout "s3://$TUTORIAL_UNIQUE_KEY-s3-bucket-1"

echo "✅ Test Lambda through API Gateway"
rest_api_id=$(aws apigateway get-rest-apis --region "$TUTORIAL_REGION" --profile aws-workout --query "items[?name == 'dev-cpu-208-api-gtw'].id" --output text)
curl "https://$rest_api_id.execute-api.$TUTORIAL_REGION.amazonaws.com/dev/demo-208"

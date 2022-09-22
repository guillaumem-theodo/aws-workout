#!/usr/bin/env bash

ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="cpu-209-ec2-test-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
echo "✅ Copy file to S3 from from within VPC - EC2 public IP: $ec2_1_public_ip"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@"$ec2_1_public_ip" \
"echo 'Hello in S3' > test.txt; aws s3 cp --region $TUTORIAL_REGION  test.txt  s3://$TUTORIAL_UNIQUE_KEY-cpu-209-s3-bucket/test.txt"

echo "✅ Test S3 ls from within VPC - EC2 public IP: $ec2_1_public_ip"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@"$ec2_1_public_ip" \
"aws s3 ls --region $TUTORIAL_REGION  s3://$TUTORIAL_UNIQUE_KEY-cpu-209-s3-bucket"

echo "✅ Test S3 cp from within VPC - EC2 public IP: $ec2_1_public_ip"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@"$ec2_1_public_ip" \
"aws s3 cp s3://$TUTORIAL_UNIQUE_KEY-cpu-209-s3-bucket/test.txt output.txt; ls -al output.txt"

echo "✅ Test S3 ls from local laptop: ls SHOULD BE OK"
aws s3 ls --region eu-west-1  --profile aws-workout "s3://$TUTORIAL_UNIQUE_KEY-cpu-209-s3-bucket"

echo "❌ Test S3 CP from local laptop: cp SHOULD BE KO"
aws s3 cp --region eu-west-1  --profile aws-workout "s3://$TUTORIAL_UNIQUE_KEY-cpu-209-s3-bucket/test.txt" output.txt

echo "✅ Test Lambda (that performs a s3Client.GetObject) through API Gateway"
rest_api_id=$(aws apigateway get-rest-apis --region "$TUTORIAL_REGION" --profile aws-workout --query "items[?name == 'dev-cpu-209-api-gtw'].id" --output text)
curl "https://$rest_api_id.execute-api.$TUTORIAL_REGION.amazonaws.com/dev/demo-209"




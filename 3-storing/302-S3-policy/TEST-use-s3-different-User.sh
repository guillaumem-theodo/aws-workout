#!/usr/bin/env bash


## Navigate in S3 buckets
echo "❌ Get/Retrieve an object from S3 with User aws-workout"
aws s3 cp --region "$TUTORIAL_REGION"  --profile aws-workout s3://unique-name-s3-bucket-1-301/my-key-1 ./tmp.txt

echo "✅ Get/Retrieve an object from S3 with User aws-workout-second-user"
aws s3 cp --region "$TUTORIAL_REGION"  --profile aws-workout-second-user s3://unique-name-s3-bucket-1-301/my-key-1 ./tmp.txt

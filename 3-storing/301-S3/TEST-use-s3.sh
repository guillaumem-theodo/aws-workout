#!/usr/bin/env bash

## Navigate in S3 buckets
echo "✅ List all buckets in ALL regions (even if we set the --region parameter)"
aws s3 ls --region "$TUTORIAL_REGION"  --profile aws-workout

echo "✅ List all objects in a bucket matching a name (unique-name-s3-bucket-2-301)"
aws s3 ls --region "$TUTORIAL_REGION"  --profile aws-workout s3://unique-name-s3-bucket-2-301

echo "✅ List objects in a sub key (mySubGroup)"
aws s3 ls --region "$TUTORIAL_REGION"  --profile aws-workout s3://unique-name-s3-bucket-1-301/mySubGroup/

echo "✅ List objects in a sub key (mySubGroup/mySecondSubGroup)"
aws s3 ls --region "$TUTORIAL_REGION"  --profile aws-workout s3://unique-name-s3-bucket-1-301/mySubGroup/mySecondSubGroup/

echo "✅ Put a new object in the bucket"
aws s3 cp --region "$TUTORIAL_REGION"  --profile aws-workout ./3-storing/301-S3/fixtures/file1.txt s3://unique-name-s3-bucket-1-301/mySubGroup/mySecondSubGroup/my-key-1

echo "✅ List objects in a sub key (mySubGroup/mySecondSubGroup) after update"
aws s3 ls --region "$TUTORIAL_REGION"  --profile aws-workout s3://unique-name-s3-bucket-1-301/mySubGroup/mySecondSubGroup/

echo "✅ Get/Retrieve an object from S3"
aws s3 cp --region "$TUTORIAL_REGION"  --profile aws-workout s3://unique-name-s3-bucket-1-301/mySubGroup/mySecondSubGroup/my-key-1 ./tmp.txt
cat ./tmp.txt
rm ./tmp.txt


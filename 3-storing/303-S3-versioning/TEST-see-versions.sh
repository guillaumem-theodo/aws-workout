#!/usr/bin/env bash

## Navigate in S3 buckets
echo "✅ List all buckets in ALL regions (even if we set the --region parameter)"
aws s3 ls --region "$TUTORIAL_REGION"  --profile aws-workout

echo "✅ List all objects in a bucket matching a name (unique-name-s3-bucket-1-303)"
aws s3 ls --region "$TUTORIAL_REGION"  --profile aws-workout s3://unique-name-s3-bucket-1-303

echo "✅ List all objects in a bucket matching a name (unique-name-s3-bucket-2-303)"
aws s3 ls --region "$TUTORIAL_REGION"  --profile aws-workout s3://unique-name-s3-bucket-2-303

echo "✅ List all versions of an object in a versioned bucket"
aws s3api list-object-versions --region "$TUTORIAL_REGION"  --profile aws-workout --bucket unique-name-s3-bucket-1-303 --prefix my-key-1

echo "✅ Retrieve one version of the object"
one_version_id=$(aws s3api list-object-versions --region "$TUTORIAL_REGION"  --profile aws-workout --bucket unique-name-s3-bucket-1-303 --prefix my-key-1 --query 'sort_by(Versions,&LastModified)[0].VersionId' --output text)
aws s3api get-object --region "$TUTORIAL_REGION"  --profile aws-workout --bucket unique-name-s3-bucket-1-303 --key my-key-1 --version-id "$one_version_id" ./tmp.txt
cat ./tmp.txt

echo "✅ Retrieve another version of the object"
one_version_id=$(aws s3api list-object-versions --region "$TUTORIAL_REGION"  --profile aws-workout --bucket unique-name-s3-bucket-1-303 --prefix my-key-1 --query 'sort_by(Versions,&LastModified)[2].VersionId' --output text)
aws s3api get-object --region "$TUTORIAL_REGION"  --profile aws-workout --bucket unique-name-s3-bucket-1-303 --key my-key-1 --version-id "$one_version_id" ./tmp.txt
cat ./tmp.txt

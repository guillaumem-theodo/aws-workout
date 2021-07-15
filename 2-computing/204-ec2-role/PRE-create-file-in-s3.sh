#!/usr/bin/env bash
aws s3 cp --region "$TUTORIAL_REGION"  --profile aws-workout README.md s3://unique-name-s3-bucket-1-203


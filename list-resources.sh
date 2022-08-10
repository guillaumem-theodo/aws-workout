#!/usr/bin/env bash
source ./backend-env.conf
aws resourcegroupstaggingapi get-resources --profile aws-workout --region $TUTORIAL_REGION --output text --tag-filters "Key=Purpose,Values=aws-workout"
aws resourcegroupstaggingapi get-resources --profile aws-workout --region $TUTORIAL_ANOTHER_REGION --output text --tag-filters "Key=Purpose,Values=aws-workout"

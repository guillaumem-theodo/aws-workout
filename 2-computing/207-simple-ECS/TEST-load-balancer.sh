#!/usr/bin/env bash

alb_url=$(aws elbv2 describe-load-balancers --region "$TUTORIAL_REGION" --profile aws-workout --names "cpu-207-alb" --query 'LoadBalancers[0].DNSName' --output text)
echo "✅ Trying to curl the ALB many times: $alb_url -  should be the same response each time"
curl "http://$alb_url"
sleep 2
curl "http://$alb_url"
sleep 2
curl "http://$alb_url"
sleep 2
curl "http://$alb_url"
sleep 2
curl "http://$alb_url"
sleep 2
curl "http://$alb_url"

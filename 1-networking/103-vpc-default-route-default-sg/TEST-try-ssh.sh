#!/usr/bin/env bash

## TRY TO SSH on EC2 --> SHOULD TIMEOUT within 10s
ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-103-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
echo "❌ Trying to SSH (should be KO after 10s) into the EC2 created in Tutorial 103 with public IP: $ec2_1_public_ip. There is no route from internet. There is no SecurityGroup allowing such traffic"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 ec2-user@"$ec2_1_public_ip"

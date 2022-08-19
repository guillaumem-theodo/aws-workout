#!/usr/bin/env bash

## TRY TO SSH on EC2 --> SHOULD BE OK
ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-104-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)

echo "✅ Trying to reach internet from within EC2 created in Tutorial 104 with public IP: $ec2_1_public_ip. Read first 200 bytes of https://pokeapi.co/api/v2/pokemon/pikachu"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@"$ec2_1_public_ip" curl -m 10 -r 0-200 https://pokeapi.co/api/v2/pokemon/pikachu

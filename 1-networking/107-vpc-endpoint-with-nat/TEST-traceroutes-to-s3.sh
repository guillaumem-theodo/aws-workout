#!/usr/bin/env bash
## RETRIEVE the IPs
ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-105-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
ec2_2_private_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-105-ec2-2"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)
nat_gtw_private_ip=$(aws ec2 describe-addresses --region "$TUTORIAL_REGION" --profile aws-workout --filters Name=tag:Name,Values="net-106-nat-gtw-eip" --query 'Addresses[0].PrivateIpAddress' --output text)

## DISPLAY the Routes to S3
echo "✅ Trace Route to S3 in region '$TUTORIAL_REGION' -> Should NOT go through NAT gateway $nat_gtw_private_ip"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -J ec2-user@"$ec2_1_public_ip" ec2-user@"$ec2_2_private_ip" sudo traceroute -n -T -p 443 -m 6 "s3.$TUTORIAL_REGION.amazonaws.com"

echo "✅ Trace Route to S3 in another region -> SHOULD go through 👉 NAT gateway $nat_gtw_private_ip"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -J ec2-user@"$ec2_1_public_ip" ec2-user@"$ec2_2_private_ip" sudo traceroute -n -T -p 443 -m 6 "s3.$TUTORIAL_ANOTHER_REGION.amazonaws.com"

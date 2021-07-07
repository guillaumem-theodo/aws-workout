#!/usr/bin/env bash

ec2_1_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-108-ec2-1"  Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)

## TRY TO GET DNS names of both EC2
ec2_1_public_dns=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-108-ec2-1" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicDnsName' --output text)
ec2_2_public_dns=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-108-ec2-2" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicDnsName' --output text)
ec2_1_private_dns=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-108-ec2-1" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateDnsName' --output text)
ec2_2_private_dns=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-108-ec2-2" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateDnsName' --output text)

echo "✅ Trying to get public DNS of first EC2 '$ec2_1_public_dns' - private DNS: $ec2_1_private_dns"
nslookup "$ec2_1_public_dns"

echo "❌ EC2 does not have public DNS name:'$ec2_2_public_dns' (missing) - private DNS: $ec2_2_private_dns"
nslookup "$ec2_2_private_dns"

echo "✅ Private DNS '$ec2_1_private_dns' can be used from with the VPC"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 ec2-user@"$ec2_1_public_ip" nslookup "$ec2_2_private_dns"

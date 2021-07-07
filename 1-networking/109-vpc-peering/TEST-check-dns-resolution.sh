#!/usr/bin/env bash

## TRY TO GET IPs of all EC2
ec2_3_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-3" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
ec2_1_private_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-1" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)
ec2_2_private_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-2" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)
ec2_3_private_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-3" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateIpAddress' --output text)

ec2_2_public_ip=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-2" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)

ec2_1_private_dns=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-1" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateDnsName' --output text)
ec2_1_public_dns=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-1" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicDnsName' --output text)
ec2_2_private_dns=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-2" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PrivateDnsName' --output text)
ec2_2_public_dns=$(aws ec2 describe-instances --region "$TUTORIAL_REGION"  --profile aws-workout --filters Name=tag:Name,Values="net-109-ec2-2" Name=instance-state-code,Values=16 --query 'Reservations[0].Instances[0].PublicDnsName' --output text)

echo "✅ DNS Resolution from private DNS ($ec2_2_private_dns) resolves to private IP ($ec2_2_private_ip) - 👉 vpc peering default setting"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 ec2-user@"$ec2_3_public_ip" nslookup "$ec2_2_private_dns"
echo "✅ DNS Resolution from public DNS ($ec2_2_public_dns) resolves by default to public IP ($ec2_2_public_ip) - 👉 vpc peering default setting"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 ec2-user@"$ec2_3_public_ip" nslookup "$ec2_2_public_dns"

echo "✅ DNS Resolution from public DNS ($ec2_1_public_dns) resolves to private IP ($ec2_2_private_ip) - 👉 if VPC peering is setup this way -e.g. peering from VPC1 to VPC2"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -J ec2-user@"$ec2_3_public_ip" ec2-user@"$ec2_2_private_ip" nslookup "$ec2_1_public_dns"
ssh -i ./aws-workout-key-pair.pem -o ConnectTimeout=10 -J ec2-user@"$ec2_3_public_ip" ec2-user@"$ec2_2_private_ip" nslookup "$ec2_1_private_dns"

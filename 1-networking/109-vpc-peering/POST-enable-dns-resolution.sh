#!/usr/bin/env bash

peering_cx_id=$(aws ec2 describe-vpc-peering-connections --region "$TUTORIAL_REGION" --profile aws-workout --filters Name=tag:Name,Values="peering-109-2-1" Name=status-code,Values=active --query 'VpcPeeringConnections[0].VpcPeeringConnectionId' --output text)

echo "🚧 Enable DNS Resolution for Peering Connection $peering_cx_id (for CloudFormation)"
aws ec2 modify-vpc-peering-connection-options --region "$TUTORIAL_REGION" --profile aws-workout --vpc-peering-connection-id "$peering_cx_id" \
  --requester-peering-connection-options AllowDnsResolutionFromRemoteVpc=true \
  --accepter-peering-connection-options AllowDnsResolutionFromRemoteVpc=true


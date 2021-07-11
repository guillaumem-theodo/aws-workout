## Add four subnets in the VPC

1️⃣ Create four non-overlapping subnets: 
- net-102-subnet-1 with nearly 256 IPs
- net-102-subnet-2 with nearly 256 IPs
- net-102-subnet-3 with nearly 4096 IPs
- net-102-subnet-4 with nearly 256 IPs

🏁 Test the Created subnets using AWS CLI `aws ec2 describe-subnets`

You select the size of the subnet using the subnet IPv4 CIDR.

⚠️ A subnet belongs to ONE VPC.

⚠️ A subnet spans in ONE and ONLY ONE REGION.

⚠️ A subnet spans in ONE and ONLY ONE AVAILABILITY ZONE in the Region.

#### Private / Public subnets
PUBLIC subnets are subnets that have a **route** from and to internet. It is NOT a flag. It is the configuration of the network (route tables...) that make the subnet PUBLIC.

PRIVATE subnets are subnets WITHOUT **route** FROM internet. Private Subnets CAN have route TO internet. 

[Doc AWS](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Subnets.html)

![Image of VPC](./doc/102-basic-subnets.png)

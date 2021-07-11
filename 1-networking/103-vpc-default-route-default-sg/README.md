## Add an EC2 in subnet net-102-subnet-1

1️⃣ Create an EC2 in the subnet

When creating a VPC, AWS provisions:
- a default RouteTable attached to the VPC
- a default Security Group (firewall) attached to the VPC

The aim of this EC2 is to see if we can connect to it with default network settings.

🏁 Test the EC2 reachability
- ✅ Observe the default `Route Table` associated with VPC net-101-vpc (`aws ec2 describe-route-tables`)
- There is no route from internet
- The only route is a `local` route (from this VPC to this VPC)  


- ✅ Observe the default `Security Group`associated with VPC net-101-vpc (`ec2 describe-security-groups`)
- There is no Security Group Rule that allows SSH (TCP port 22) or ping (ICMP)
- The only Security Group Rule allows communication from this Security Group (self)


- ❌ Observe that with this setting it is not possible to SSH the created EC2

![Image of VPC](./doc/103-vpc-default-route-default-sg.png)

## Make EC2 reachable from internet

- Add an `Internet Gateway` (net-104-igw) attached to the VPC (`net-101-vpc`)
- Create a Route Table `net-104-rt`
- Add route to/from internet (0.0.0.0/0) in the Route Table
- Create a Security Group `net-104-sg`
- Start an EC2 in this subnet with this security group
- Observe routes in the new Route Table
- Observe security group rules in the new Security Group
- Test PING using EC2's public IP
- Test SSH using EC2's public IP
- From withing EC2 (while ssh-in), try to reach internet (outgoing traffic)

![Image of VPC](./doc/104-internet-access.png)

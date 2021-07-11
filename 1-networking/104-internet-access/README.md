## Make EC2 reachable from internet

1️⃣ Add an `Internet Gateway` (net-104-igw) attached to the VPC (`net-101-vpc`)

2️⃣ Make the subnet reachable (route and security) from Internet through the Internet Gateway
- Create a Route Table `net-104-rt`
- Add route to/from internet (0.0.0.0/0) in the Route Table
- Attach this route table to the subnet
👉 Now the subnet is PUBLIC  

- Create a Security Group `net-104-sg`
  
3️⃣ Start an EC2 within this subnet with this security group
  
🏁 Tests EC2 reachability
- Observe routes in the new Route Table
- Observe security group rules in the new Security Group
- Test PING using EC2's public IP
- Test SSH inside EC2 using public IP
- From withing EC2 (while ssh-in), try to reach internet (outgoing traffic)

[Doc AWS](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Internet_Gateway.html)

![Image of VPC](./doc/104-internet-access.png)

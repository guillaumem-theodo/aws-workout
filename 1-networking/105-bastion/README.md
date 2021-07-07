## Create a BASTION architecture

In a first subnet (public subnet `bastion`):
- create a route table routing traffic from/to internet through NAT Gateway
- create a security group allowing traffic for SSH and PING from your laptop (myIP/32)
- start an EC2 with a public IP inside this subnet

In a second subnet (private subnet)
- create a route table routing traffic from the VPC through local
- create a security group allowing traffic on any port ONLY from the previous security group
- start a second EC2 WITHOUT public IP inside this subnet

- Observe that you can SSH in the first EC2 from your laptop
- Observe that you CAN'T SSH in the first EC2 from another laptop (or from a VPN)  
- Observe that you can SSH in the second EC2 ONLY from the first EC2
- Observe that you can't SSH in the second EC2 from your laptop (anyway there is no public IP)
- Observe that you can reach internet from the first EC2
- Observe that you CAN'T reach internet from the second EC2


![Image of VPC](./doc/105-bastion.png))

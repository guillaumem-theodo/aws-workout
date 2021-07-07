## Add an OUTGOING (egress) access to Internet from private subnets

⚠️ From the Bastion DOJO, 

In the first subnet (public subnet)
- create an Elastic IP `net-106-nat-gtw-eip`
- create a NAT Gateway (with the attached Elastic IP) `net-106-nat-gtw`

In the second subnet (private subnet)
- in the route table
- create a route to internet (0.0.0.0/0) through NAT Gateway (in the public subnet)

- Observe that you can SSH in the first EC2 from your laptop
- Observe that you can SSH in the second EC2 ONLY from the first EC2
- Observe that you can reach internet from the first EC2
- Observe that you can reach internet from the second EC2
- Using Traceroute command, verify that outgoing traffic from first EC2 goes through Internet Gateway
- Using Traceroute command, verify that outgoing traffic from second EC2 goes through NAT Gateway. The first HOP in the route should be the NAT private IP



![Image of VPC](./doc/106-nat-gtw.png)

![First Hop](./doc/nat-gateway-first-hop.png)


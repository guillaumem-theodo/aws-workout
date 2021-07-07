## VPC Peering

Create Three VPC with **non overlapping** CIDR
- All VPC with DNS enabled
- Create an EC2, with public IP in all VPC, in default subnet and security group
- Add route to/from internet for VPC 3
- Allow SSH in SG for VPC 3


- Add a VPC Peering connection, auto-accepted, between VPC 3 and VPC 2
- Add a VPC Peering connection, auto-approved, between VPC 2 and VPC 1
- For this second VPC Peering Connection, enable private DNS resolution
- Update the route tables to route traffic from/to other VPCs through peering connection


- Observe that EC2 in VPC 2 is reachable from EC2 in VPC 1 using private IPs
- Observe that EC2 in VPC 1 is reachable from EC2 in VPC 2 using private IPs
- Observe that this connection is also possible in the other way
- Observe that EC2 in VPC 1 IS NOT ❌ reachable from VPC 3: VPC peering IS NOT transitive

For DNS resolution
- Observe that DNS lookup of the private DNS names always resolve to private IP
- Observe that DNS lookup of the public DNS name resolves to public IP
- Except for resolution of public DNS names of VPC 1 from VPC 2, that resolves to private IPs

  

![DNS](./doc/109-vpc-peering.png)

PEERING TESTS
![PEERING](./doc/peering.png)

DNS RESOLUTION TESTS
![DNS](./doc/dns.png)

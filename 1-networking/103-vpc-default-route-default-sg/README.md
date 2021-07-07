## Add an EC2 in subnet net-102-subnet-1

- Observe the default `Route Table` associated with VPC net-101-vpc
- Observe the default `Security Group`associated with VPC net-101-vpc
- Observe that with this setting it is not possible to SSH the created EC2
- There is no route from internet
- The only route is a `local` route (from this VPC to this VPC)  
- There is no Security Group Rule that allows SSH (TCP port 22) or ping (ICMP)
- The only Security Group Rule allows communication from this Security Group (self)

![Image of VPC](./doc/103-vpc-default-route-default-sg.png)

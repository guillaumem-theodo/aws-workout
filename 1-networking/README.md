### NETWORKING

- [101-basic-vpc](./101-basic-vpc): Create a simple 👉 **VPC** / Understand CIDR
- [102-basic-subnets](./102-basic-subnets) : Create 👉 **subnets**
- [103-vpc-default-route-default-sg](./103-vpc-default-route-default-sg):
    - View 👉 **Default** Route associated with a VPC (by default)
    - View 👉 **Default** Security Group associated with a VPC (by default)
    - Create Two EC2 (computation instances) in subnets
    - Show that EC2 can't be SSH
- [104-internet-access](./104-internet-access): Add an 👉 **Internet Gateway**
    - Create a Route to Internet through Internet Gateway
    - Create a Security Group that allows incoming traffic on port SSH, ICMP from internet
    - EC2 can be SSH
    - EC2 can be Ping
    - EC2 can reach internet
- [105-bastion](./105-bastion): Create a 👉 **BASTION**
    - One EC2 (Bastion EC2) is in a public subnet with access FROM your IP Only
    - Another EC2 (Private EC2) is in a private subnet and can only be accessed from the first EC2
    - BASTION EC2 can be Ping (from your IP)
    - BASTION EC2 can be ssh (from your IP)
    - BASTION EC2 can reach Internet (outgoing)
    - PRIVATE EC2 can be ssh from BASTION (only)
    - PRIVATE EC2 can't reach Internet
- [106-nat-gtw](./106-nat-gtw): Add a 👉 **NAT Gateway** to allow PRIVATE EC2 to reach internet (outgoing only)
    - PRIVATE EC2 can now reach Internet
    - Show Traceroute to internet from Bastion EC2: through Internet Gateway
    - Show Traceroute to internet from Private EC2: through NAT Gateway
- [107-vpc-endpoint](./107-vpc-endpoint): Create a 👉 **S3 VPC Gateway Endpoint** to reach S3 privately
    - Create a VPC Gateway Endpoint to S3 buckets in one region
    - Show traceroute to S3 bucket in the same region: use direct VPC Endpoint
    - Show traceroute to S3 bucket in another region: use NAT Gateway (and thus internet)
- [108-dns](./108-dns): Show that VPC contains 👉 **VPC DNS**
    - it provides DNS names to EC2
    - it provides DNS resolution to find EC2 (either using public DNS names or private ones)  
    - it can be disabled
- [109-vpc-peering](./109-vpc-peering): Show that VPCs can be peered using 👉 **VPC PEERING**
    - VPC peering allows communicating between VPCs (using private IPs)
    - VPC peering relies on VPC Peering Connection
    - VPC peering relies on route tables too
    - VPC peering IS ❌ NOT TRANSITIVE !!! 
    - VPC peering and DNS resolution
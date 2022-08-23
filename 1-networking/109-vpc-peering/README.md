# VPC Peering

By default, **VPCs can't communicate to each others**.  
VPC Peering is one way (among many others) to make VPC reachable from each others.

👉 VPC Peering is a **one-to-one** connection.
- Peering is NOT transitive. If VPC A is peered to VPC B, and if B is peered to C, **A CAN'T reach C**.
- There is NO broadcast. There is no way to peer A with (B and C) using ONE VPC Peering connection.
 
If you need more complex VPCs connectivity you should have a look on **VPC Transit Gateway** (for example).

## Your mission

1️⃣ Create Three VPC with **non overlapping** CIDR
- All VPC with DNS enabled (see Workouts 108 for more details)
- In each VPC create a public subnet
- In VPC3 create an Internet Gateway (you can use CDK helpers)

2️⃣ Create an EC2, with public IP in each VPC
- Add route to/from internet for VPC 3 (with Terraform you can use default route table, with CDK it's easier to create a new route table)
- Allow SSH and Ping in Security Groups (with Terraform you can use and modify the default SG ; with CDK it's easier to create a new SecurityGroup)

EC2 in VPC 3 will be accessible from internet (so you can SSH in and test connectivity from it).

3️⃣ Create VPC peering connections
- Add a VPC Peering connection, auto-approved, between VPC 3 and VPC 2
- Add a VPC Peering connection, auto-approved, between VPC 2 and VPC 1
- For this second VPC Peering Connection, enable **private DNS resolution**
- Update the route tables to route traffic from/to other VPCs through peering connection

<div align="center">
<img src="./doc/109-vpc-peering.png" width="900" alt="Peering">
</div>
<br>

#### Some additional help...

> For **CDK workouts**, you can rely on following code (class ``SecurityGroupHelper`` provided in the tutorial) to create Security Groups
> ```java 
> CfnSecurityGroup sg1 = createSecurityGroup(this, vpc1, "net-108-sg-1");
> ```

> For **CDK workouts**, you can rely on following code (class ``EC2Helper`` provided in the tutorial) to create EC2 (and make your code DRY)
> ```java 
> EC2Helper.createEC2(this, subnet, securityGroup, name, true, instanceProfile);
> ```

> For **CDK workouts**, you can rely on following code (class ``InternetGatewayHelper`` provided in the tutorial) to create and attach InternetGateway to VPC
 > ```java 
 > CfnInternetGateway internetGateway = InternetGatewayHelper.createAndAttachInternetGateway(this, vpc1, "net-108-igw");
 > InternetGatewayHelper.createAndAttachRouteTableToSubnet(...);
 > ```

In order to modify DNS attributes on VPC Peering, Terraform offers a parameter directly into its construct.
> For **Terraform workouts**, you can use following construct
> ```hcl 
> resource "aws_vpc_peering_connection" "net-109-peering-2-1" {
>  vpc_id = // TODO something
>  peer_vpc_id = // TODO something
>  auto_accept = true
>
>  accepter {
>    allow_remote_vpc_dns_resolution = true
>  }
>
>  requester {
>    allow_remote_vpc_dns_resolution = true
>  }
> }
> ```

But for CDK Workouts, such construct does not exist (at least with L1 construct), you must create a CDK Custom Resource to trigger AWS API calls.

> For **CDK workouts**, you can use following code based on ``AllowVPCPeeringDNSResolution`` Custom Resource provided for you
> ```java 
> ...
> CfnVPCPeeringConnection vpcPeering = createVpcPeering(vpc2, vpc1, routeTable2, routeTable1, securityGroup2, securityGroup1, "net-109-peering-2-1");
> new AllowVPCPeeringDNSResolution(this, "net-109-peering-2-1-options", vpcPeering);  // <<<-- This CODE is a CUSTOM CDK Resource provided for you
> ```

## Your success
🏁 Test VPCs reachability from each others
- ✅ Observe that EC2 in VPC 2 is reachable from EC2 in VPC 1 using private IPs
- ✅ Observe that EC2 in VPC 1 is reachable from EC2 in VPC 2 using private IPs
- ✅ Observe that this connection is also possible in the other way
- ❌ Observe that EC2 in VPC 1 IS NOT directly reachable from VPC 3: VPC peering IS NOT transitive

<div align="center">
<img src="./doc/peering.png" width="900" alt="Peering Tests">
</div>
<br>

For DNS resolution
- ✅ Observe that DNS `nslookup` of the **private host name** always resolve to **private IP**
- ✅ Observe that DNS `nslookup` of the **public host name** resolves to **public IP**
- ✅ Except for resolution of public DNS host names of VPC 1 from VPC 2, that resolves to private IPs

<div align="center">
<img src="./doc/dns.png" width="900" alt="DNS Resolution">
</div>
<br>

You can use following commands to check your mission success
```shell
./launch.sh 1-networking/109-vpc-peering/TEST-check-peering-connections.sh
./launch.sh 1-networking/109-vpc-peering/TEST-check-dns-resolution.sh
```


## Support

# VPC with or without DNS Support

AWS VPC come with AWS internal **DNS service**.  
DNS Service allows you to translate a hostname (a plain text understandable host identifier) to an IP address (either public IP or private IP).  
When creating a VPC you can enable or disable DNS service.

- You can enable/disable DNS resolution service.
- You can enable/disable DNS naming of your EC2s.

Behind the scene, the DNS service is located at IP: `CIDR + 2`
Example: if your VPC CIDR is `10.0.0.0/24`. The DNS service will be at IP: `10.0.0.2`

## Your mission

1️⃣ Create Two VPC (with non overlapping CIDR)
- One VPC with DNS Support enabled
- One VPC without DNS Support

2️⃣ Launch one EC2 in each VPC

<div align="center">
<img src="./doc/108-dns.png" width="900" alt="DNS">
</div>
<br>

#### Some additional help...
For EC2s, you will have to select subnet and security groups...allowing you to SSH into

> For **Terraform workouts**, you can use default subnet and security groups and modify them
> ```hcl 
> resource "aws_security_group_rule" "net-108-sg-1-ssh" {
>  type = "ingress"
>  cidr_blocks = ["0.0.0.0/0"]
>  security_group_id = aws_vpc.net-108-vpc-1.default_security_group_id
>  ...
> }
> ```

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

## Your success
🏁 Observe the DNS names and resolution of EC2s
- ✅ The EC2 in the first VPC has a public and a private DNS name
- ✅ The EC2 in the second VPC has ONLY a private DNS name
- ✅ Using `nslookup` command, from your laptop, try to get the public IP from the public DNS name
- ❌ Using `nslookup` command, from your laptop try to get the private IP from the private DNS name
- ✅ Using `nslookup` command, from inside the first EC2, get the IP of the second EC2 using its private name. Resolve to the private IP.
- ✅ Using `nslookup` command, from inside the first EC2, get the IP of the second EC2 using its public name. Resolve to the private IP.

<div align="center">
<img src="./doc/dns.png" width="900" alt="DNS Resolution">
</div>
<br>

You can use following commands to check your mission success
```shell
./launch.sh 1-networking/108-dns/TEST-check-dns.sh
```

## Materials

[Doc AWS](https://docs.aws.amazon.com/vpc/latest/userguide/vpc-dns.html)


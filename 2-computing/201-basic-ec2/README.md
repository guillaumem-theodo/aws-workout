## A Simple EC2 

Your are going to deploy an EC2 inside a VPC.  
Amazon Elastic Compute Cloud (Amazon EC2) provides scalable computing capacity in the Amazon Web Services (AWS) Cloud.  
EC2 are deployed in a VPC (and thus inside a region, and inside an IP addresses range).  
EC2 are secured by:
- Network ACL (see tutorial `1-networking/103`). Firewall at a subnet level (that applies to all EC2s deployed in the subnet)
- Security Groups (see tutorial `1-networking/103`). Firewall at the EC2 instance level (that applies to EC2 attached to the security group)

## Your mission

1️⃣ Create a networking infrastructure (VPC, subnets, RouteTable, SecurityGroup, Internet Access)
- Use VPC and Subnets from `1-networking/101` and `1-networking/102` tutorials
- Add an Internet Gateway and accesses to/from internet (see `1-networking/104` tutorial)
- In a new Security group, Allow ports:
  - SSH only **from your IP**
  - ICMP only **from your IP**
  - 👉 HTTP from everywhere

2️⃣ Create an EC2 (see `1-networking/109` or `1-networking/108` tutorials)

<div align="center">
<img src="./doc/201-basic-ec2.png" width="800" alt="EC2">
</div>
<br>

#### Some additional help...
> For **CDK workouts**, you can rely on following code (class ``InternetGatewayHelper`` provided in the tutorial) to create and attach InternetGateway to VPC
 > ```java 
 > CfnInternetGateway igw = InternetGatewayHelper.createAndAttachInternetGateway(this, vpc1, "cpu-201-igw");
 > InternetGatewayHelper.createAndAttachRouteTableToSubnet(...);
 > ```

> For **CDK workouts**, you can rely on following code (class ``SecurityGroupHelper`` provided in the tutorial) to create Security Groups
> ```java 
> CfnSecurityGroup sg1 = createSecurityGroup(this, vpc1, "cpu-201-sg-1", HTTP);
> ```

For the public security group, you should allow only your IP address (for ping and ssh). In order to get your IP address:

> For **Terraform workouts**, you can use **myip** (module provided in the tutorial) to get it
> ```hcl 
> cidr_blocks = ["${module.myip.address}/32" ]
> ```

> For **CDK workouts**, you can rely on following code (class ``IpChecker`` provided in the tutorial) to get it
> ```java 
> String myIPAddressCIDR = IpChecker.getMyIPAddressCIDR();
> ```

> For **CDK workouts**, you can rely on following code (class ``EC2Helper`` provided in the tutorial) to create EC2 (and make your code DRY)
> ```java 
> EC2Helper.createEC2(this, subnet, securityGroup, name, true, instanceProfile);
> ```

## Your success
🏁 Tests EC2s reachability
- ✅ Test that you can SSH in the EC2 from your IP (and only your IP)
- ✅ Test that you can ping the EC2 from your IP (and only from your IP)
- ✅ Test that you can ping (`netcat`) the port 80 (HTTP) of the EC2. 
- ❌ Test that the port 443 (or any others) is not opened

You can use following commands to check your mission success
```shell
./launch.sh 2-computing/201-basic-ec2/TEST-ping-ec2.sh
./launch.sh 2-computing/201-basic-ec2/TEST-ssh-public-ec2.sh
```

## Materials
[Doc AWS](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)

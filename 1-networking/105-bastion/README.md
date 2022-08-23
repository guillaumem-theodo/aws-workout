# Create a BASTION architecture

## Your mission
A Bastion is a network construct with:
- A public EC2 server (Bastion) that allows only SSH from your company IPs. 
  - The Bastion Server is in a PUBLIC subnet and has a public IP.
  - The Bastion Server allows jumping to other private EC2s (SSH)
  - The Bastion EC2 SecurityGroup allow incoming traffic from your own IP only
- Private EC2 servers deployed in private subnets. 
  - Private EC2s haven't public IPs
  - Private EC2s SecurityGroups allow incoming traffic from Bastion EC2 (only)

1️⃣ In a first subnet (public subnet `net-102-subnet-1`):
- create a route table routing traffic from/to internet through Internet Gateway
- create a security group allowing traffic for SSH and PING from your laptop (myIP/32)
- start an EC2 **WITH** a public IP inside this public subnet

2️⃣ In a second subnet (private subnet `net-102-subnet-2`)
- create a route table routing traffic from the VPC through local (only)
- create a security group allowing traffic on any port ONLY from the previous security group (bastion)
- start a second EC2 **WITHOUT** public IP inside this subnet

<div align="center">
<img src="./doc/105-bastion.png" width="900" alt="Image of VPC">
</div>
<br>

#### Some additional help...
For the public security group, you should allow only your IP address (for ping and ssh). In order to get your IP address:

> For **Terraform workouts**, you can use **myip** (module provided in the tutorial) to get it
> ```hcl 
> cidr_blocks = ["${module.myip.address}/32" ]
> ```

> For **CDK workouts**, you can rely on following code (class ``IpChecker`` provided in the tutorial) to get it
> ```java 
> String myIPAddressCIDR = IpChecker.getMyIPAddressCIDR();
> ```

> For **CDK workouts** you can rely on a Helper Class ``EC2Helper`` (provided in this tutorial) to create EC2s.  
> This way, your CDK code will be shorter and DRY

## Your success
🏁 Tests EC2s reachability
- ✅ Observe that you can SSH in the first EC2 from your laptop
- ❌ Observe that you CAN'T SSH in the first EC2 from another laptop (or from a VPN)  
- ✅ Observe that you can SSH in the second EC2 ONLY from the first EC2
- ❌ Observe that you can't SSH in the second EC2 from your laptop (anyway there is no public IP)
- ✅ Observe that you can reach internet from the first EC2
- ❌ Observe that you CAN'T reach internet from the second EC2

You can use following commands to check your mission success
```shell
./launch.sh 1-networking/105-bastion/TEST-ssh-public-ec2.sh
./launch.sh 1-networking/105-bastion/TEST-ssh-private-ec2.sh
./launch.sh 1-networking/105-bastion/TEST-try-internet-accesses.sh
```

The fact that the private EC2 can't reach internet MAY BE a problem. 
For example for OS updates or packages updates. We will see in next workout how to fix this issue without exposing the EC2 on internet. 

## Materials


# Add an OUTGOING (egress) access to Internet from private subnets

## Your mission
⚠️ From the Bastion Workout.  

As we saw in `104 Bastion` workout, the private EC2s do not have outgoing Internet access. This may be an issue to upload OS updates or packages updates.

We are going to add a [NAT gateway](https://docs.aws.amazon.com/vpc/latest/userguide/vpc-nat-gateway.html) deployed in a public subnet to provide an OUTGOING access only to private EC2.
NAT components are ``Network Address Translation`` systems. NAT provide an outgoing access to internet. The NAT gateway replaces the source IP address (private) of the instances with the IP address of the NAT gateway.

There are three types of NAT components:
- **NAT Gateway**: managed by AWS (no need to manage servers on your side). Usable for IP v4.
- **NAT Instances**: special EC2 (with particular AMI) managed by you, that provides NAT functions. Usable for IP v4.
- **egress only Internet Gateway**: managed by AWS. **Usable ONLY for IP v6**.

1️⃣ In the public subnet (``net-102-subnet-1``)
- Create an Elastic IP `net-106-eip`
- Create a NAT Gateway (with the attached Elastic IP) `net-106-nat-gtw`

2️⃣ In the private subnet (``net-102-subnet-2``)
- In the route table, create a route to internet (0.0.0.0/0) through NAT Gateway (in the public subnet)

<div align="center">
<img src="./doc/106-nat-gtw.png" width="900" alt="Image of VPC">
</div>
<br>

## Your success
🏁 Test OUTGOING internet access from private EC2
- ✅ Observe that you can SSH in the first EC2 from your laptop
- ✅ Observe that you can SSH in the second EC2 ONLY from the first EC2
- ✅ Observe that you can reach internet from the first EC2
- ✅ Observe that you can reach internet from the second EC2
- ✅ Using Traceroute command, verify that outgoing traffic from first EC2 goes through Internet Gateway
- ✅ Using Traceroute command, verify that outgoing traffic from second EC2 goes through NAT Gateway. The first HOP in the route should be the NAT private IP

<div align="center">
<img src="./doc/nat-gateway-first-hop.png" width="900" alt="First Hop in Route">
</div>
<br>

You can use following commands to check your mission success
```shell
./launch.sh 1-networking/106-nat-gtw/TEST-ssh-public-ec2.sh
./launch.sh 1-networking/106-nat-gtw/TEST-ssh-private-ec2.sh
./launch.sh 1-networking/106-nat-gtw/TEST-traceroute.sh
./launch.sh 1-networking/106-nat-gtw/TEST-try-internet-accesses.sh
```

## Materials

[Doc AWS](https://docs.aws.amazon.com/vpc/latest/userguide/vpc-nat.html)


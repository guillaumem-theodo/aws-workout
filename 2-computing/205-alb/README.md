## ALB - Application Load Balancer

⭐⭐⭐ (more complexe)️ ⭐⭐⭐

👉 From the 101-basic-vpc and 102-basic-subnets

You will have to combine many topics seen in previous exercises.

1) Create a BASTION architecture

- with 1 Bastion EC2 located in subnet `net-102-subnet-1`
- the Bastion EC2 must be launched within a Security Group `cpu-205-sg-2`
- the Bastion Security Group `cpu-205-sg-2` must allow incoming SSH from your laptop only
- the Bastion Security Group `cpu-205-sg-2` must allow outgoing traffic to everywhere

2) Create workers EC2 (three workers)

- the workers must be located in subnets `net-102-subnet-3` and/or `net-102-subnet-4` (put 2 workers in one subnet, the remaining worker in the other subnet)
- each worker must start an HTTPD server like in tutorial `202`
- the workers must be launched within a Private Security Group `cpu-205-sg-3`
- the Private Security Group `cpu-205-sg-3` must allow incoming HTTP from the Public Security Group `cpu-205-sg-1`
- the Private Security Group `cpu-205-sg-3` must allow incoming SSH from the Bastion Security Group `cpu-205-sg-2`
- the Private Security Group `cpu-205-sg-3`  must allow outgoing traffic to everywhere

3) Install Internet Gateway for Internet access (in and out)

- the IGW must be accessible from the public and bastion subnets (`net-102-subnet-1` and `net-102-subnet-2`)
- you need to add a Route to `0.0.0.0/0` through Internet Gateway to a new Route Table `cpu-205-rt-1`
- associate the Route Table `cpu-205-rt-1` to `net-102-subnet-1` and `net-102-subnet-2` subnets

4) Install NAT Gateway for Internet access (out only) for Workers

Workers need to upload and install HTTPD yum packages. They need outgoing internet access.
   
- the NAT Gateway must be created in a public subnet (for example `net-102-subnet-1`)
- you need to add a Route to `0.0.0.0/0` through NAT Gateway to a new Route Table `cpu-205-rt-2`
- associate the Route Table `cpu-205-rt-2` to `net-102-subnet-3` and `net-102-subnet-4` private subnets

5) Create an Application Load Balancer (ALB)

- the ALB must be created in (at least two) public subnets (`net-102-subnet-1` and `net-102-subnet-2`) 
- the ALB must be launched within a public security group `cpu-205-sg-1`
- the public security group `cpu-205-sg-1` must allow incoming HTTP from everywhere
- the public security group `cpu-205-sg-1` must allow any outgoing traffic to everywhere
- create a **Target Group**
- attach the three EC2 workers as **Targets** to the Target Group
- add a **listener on port 80** on the ALB, that forward traffic to the Target Group

### Organise your files
You can split the TERRAFORM file, in multiple files in the same directory.
You can create:
- a `main.tf` file with the ALB
- a `network.tf` file with the subnets and routes/route tables
- a `security.tf` file with the security groups
- a `workers.tf` file with the EC2


![Image of VPC](./doc/205-alb.png)





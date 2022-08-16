########################################################################################################################
variable "vpc_id" {
  type = string
}

variable "public_subnet_102_id" {
  type = string
}

variable "private_subnet_102_id" {
  type = string
}

variable "ec2_profile_instance_id" {
  type = string
}

######################################################################################
## Create a BASTION architecture
## 1) create an internet gateway (IGW) for public access to/from internet (for the public subnet)
## 2) create a route table and a route to 0.0.0.0 via IGW (associated to the public subnet)
## 3) authorize PING and SSH in a security group (for the public subnet)
## 4) associate the security group to the BASTION EC2 instance
## 5) create a route table from bastion subnet to private subnet (local vpc)
## 6) authorize all traffic from bastion subnet (only) TO private subnet (within a security group)
## 7) associate the security group to the PRIVATE EC2 instances

## Internet Gateway is a BIDIRECTIONAL gateway to Internet from VPC
resource "aws_internet_gateway" "net-105-igw" {
  vpc_id = var.vpc_id
  tags = {
    Purpose: var.dojo
    Name: "net-105-igw"
  }
}

## Create a ROUTE TABLE associated to the VPC
## The route table routes all traffic from/to internet through Internet gateway
## The route table is associated to the subnet
resource "aws_route_table" "net-105-rt-1" {
  vpc_id = var.vpc_id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.net-105-igw.id
  }

  tags = {
    Purpose: var.dojo
    Name: "net-105-rt-1"
  }
}

resource "aws_route_table_association" "rt-association-subnet1-105" {
  route_table_id = aws_route_table.net-105-rt-1.id
  subnet_id = var.public_subnet_102_id
}

## Create a PUBLIC SECURITY GROUP associated to the VPC
## The SG allows all incoming PORT 22 traffic from your laptop
## The SG allows all incoming PORT ICMP (ping) traffic from Internet (0.0.0.0/0) - bad habit but for demo purpose

resource "aws_security_group" "net-105-sg-1" {
  vpc_id = var.vpc_id

  ## ALL SSH AND PING INCOMING TRAFFIC ENTERING THE SECURITY GROUP COMING FROM YOUR LAPTOP
  ingress {
    from_port = 22
    protocol = "tcp"
    to_port = 22
    cidr_blocks = ["${module.myip.address}/32" ]
  }
  ingress {
    from_port = -1
    protocol = "icmp"
    to_port = -1
    cidr_blocks = ["${module.myip.address}/32" ]
  }
  ## ALL OUTGOING TRAFFIC INITIATED FROM THE SECURITY GROUP
  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  tags = {
    Purpose: var.dojo
    Name: "net-105-sg-1"
  }
}

## CREATE an EC2 inside the BASTION subnet (with the associated route table) and inside the security group
## As a consequence the EC2 should be reachable (ping and SSH) from your laptop (only)
## And EC2 can initiate traffic to internet (curl...)
resource "aws_instance" "net-105-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = var.public_subnet_102_id
  security_groups = [aws_security_group.net-105-sg-1.id]
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-105-ec2-1"
    Description: "BASTION EC2 in a subnet with a route and a security group (in BASTION subnet)"
  }
}

## The route table routes all traffic from/to Bastion SUBNET
## The route table is associated to the private subnet
resource "aws_route_table" "route-table-105-2" {
  vpc_id = var.vpc_id

  tags = {
    Purpose: var.dojo
    Name: "net-105-rt-2"
  }
}

resource "aws_route_table_association" "rt-association-subnet2-105" {
  route_table_id = aws_route_table.route-table-105-2.id
  subnet_id = var.private_subnet_102_id
}

## Create a PUBLIC SECURITY GROUP associated to the VPC
## The SG allows all incoming PORT 22 traffic from the Bastion

resource "aws_security_group" "net-105-sg-2" {
  vpc_id = var.vpc_id

  ## ALL  TRAFFIC ENTERING THE SECURITY GROUP COMING FROM THE BASTION (on ONLY from the Bastion)
  ingress {
    from_port = 0
    protocol = "-1"
    to_port = 0
    security_groups = [aws_security_group.net-105-sg-1.id]
  }
  ## ALL OUTGOING TRAFFIC INITIATED FROM THE SECURITY GROUP
  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  tags = {
    Purpose: var.dojo
    Name: "net-105-sg-2"
  }
}

## CREATE an EC2 inside the PRIVATE subnet (with the associated route table) and inside the security group
## As a consequence the EC2 should be reachable ONLY from bastion network
## PRIVATE EC2 DOES NOT HAVE PUBLIC IP
## PRIVATE EC2 CAN'T INITIATE TRAFFIC TO INTERNET

resource "aws_instance" "net-105-ec2-2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = false
  subnet_id = var.private_subnet_102_id
  security_groups = [aws_security_group.net-105-sg-2.id]
  key_name = "aws-workout-key"
  iam_instance_profile = var.ec2_profile_instance_id

  tags = {
    Purpose: var.dojo
    Name: "net-105-ec2-2"
  }
}

output "net-105-ec2-1-public-ip" {
  value = aws_instance.net-105-ec2-1.public_ip
}

output "net-105-ec2-2-private-ip" {
  value = aws_instance.net-105-ec2-2.private_ip
}

output "net-105-rt-2-id" {
  value = aws_route_table.route-table-105-2.id
}

output "net-105-sg-2-id" {
  value = aws_security_group.net-105-sg-2.id
}

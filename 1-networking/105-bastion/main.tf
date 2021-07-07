## USE S3 BUCKET TO STORE TERRAFORM STATE
terraform {
  backend "s3" {
  }
}

########################################################################################################################
## INPUTS
########################################################################################################################
## NAME OF THE TUTORIAL
variable "dojo" {
  type = string
  default = "aws-workout"
}
## REGION WHERE THE AWS COMPONENTS WILL BE DEPLOYED
variable "region" {
  type = string
  default = "eu-west-2"
}

## REGION OF THE S3 BUCKET USED TO STORE TERRAFORM STATES
variable "tf-s3-region" {
  type = string
  default = "eu-west-2"
}

## NAME OF THE S3 BUCKET USED TO STORE TERRAFORM STATES
variable "tf-s3-bucket" {
  type = string
}

########################################################################################################################
provider "aws" {
  region = var.region
  profile = "aws-workout"
}

data "terraform_remote_state" "vpc-101" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    region = var.tf-s3-region
    key = "101-basic-vpc"
  }
}
data "terraform_remote_state" "subnets-102" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    key = "102-basic-subnets"
    region = var.tf-s3-region
  }
}

data "aws_ami" "amazon-linux" {
  owners = ["amazon"]
  most_recent = true

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}

######################################################################################
#### RETRIEVE MY IP for the BASTION SG
######################################################################################
module myip {
  source  = "4ops/myip/http"
  version = "1.0.0"
}

######################################################################################
## Create a BASTION architecture
## 1) create an internet gateway (IGW) for public access to/from internet (for the public subnet)
## 2) create a route table and a route to 0.0.0.0 via IGW (for the public subnet)
## 3) authorize PING and SSH in a security group (for the public subnet)
## 4) associate the security group to the BASTION EC2 instances
## 5) create a route table from bastion subnet to private subnet (local vpc)
## 6) authorize all traffic from bastion subnet (only) TO private subnet (within a security group)
## 7) associate the security group to the PRIVATE EC2 instances

## Internet Gateway is a BIDIRECTIONAL gateway to Internet from VPC
resource "aws_internet_gateway" "igw-105" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  tags = {
    Purpose: var.dojo
    Name: "net-105-igw"
    Description: "An Internet Gateway (both direction) attached to VPC ${data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id} "

  }
}

## Create a ROUTE TABLE associated to the VPC
## The route table routes all traffic from/to internet through Internet gateway
## The route table is associated to the subnet
resource "aws_route_table" "route-table-105-1" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw-105.id
  }

  tags = {
    Purpose: var.dojo
    Name: "net-105-rt-1"
  }
}

resource "aws_route_table_association" "rt-association-subnet1-105" {
  route_table_id = aws_route_table.route-table-105-1.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
}

## Create a PUBLIC SECURITY GROUP associated to the VPC
## The SG allows all incoming PORT 22 traffic from your laptop
## The SG allows all incoming PORT ICMP (ping) traffic from Internet (0.0.0.0/0) - bad habit but for demo purpose

resource "aws_security_group" "bastion-sg-105-1" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

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
resource "aws_instance" "bastion-ec2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
  security_groups = [aws_security_group.bastion-sg-105-1.id]
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
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  tags = {
    Purpose: var.dojo
    Name: "net-105-rt-2"
  }
}

resource "aws_route_table_association" "rt-association-subnet2-105" {
  route_table_id = aws_route_table.route-table-105-2.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-2-id
}

## Create a PUBLIC SECURITY GROUP associated to the VPC
## The SG allows all incoming PORT 22 traffic from the Bastion

resource "aws_security_group" "bastion-sg-105-2" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  ## ALL  TRAFFIC ENTERING THE SECURITY GROUP COMING FROM THE BASTION
  ingress {
    from_port = 0
    protocol = "-1"
    to_port = 0
    security_groups = [aws_security_group.bastion-sg-105-1.id]
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

resource "aws_instance" "private-ec2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = false
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-2-id
  security_groups = [aws_security_group.bastion-sg-105-2.id]
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-105-ec2-2"
    Description: "PRIVATE EC2 in a subnet with a route and a security group (in PRIVATE subnet)"
  }
}

output "net-105-ec2-1-public-ip" {
  value = aws_instance.bastion-ec2.public_ip
}

output "net-105-ec2-2-private-ip" {
  value = aws_instance.private-ec2.private_ip
}

output "net-105-rt-2-id" {
  value = aws_route_table.route-table-105-2.id
}

output "net-105-sg-2-id" {
  value = aws_security_group.bastion-sg-105-2.id
}

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
## Create an access FROM and TO internet on our EC2 (public EC2)
## 1) create an internet gateway (IGW)
## 2) create a route table and a route to 0.0.0.0 via IGW
## 3) authorize HTTP, PING and SSH in a security group
## 4) associate the security group to the EC2 instances

## Internet Gateway is a BIDIRECTIONAL gateway to Internet from VPC
resource "aws_internet_gateway" "igw-201" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  tags = {
    Purpose: var.dojo
    Name: "cpu-201-igw"
    Description: "An Internet Gateway (both direction) attached to VPC ${data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id} "

  }
}

## Create a ROUTE TABLE associated to the VPC
## The route table routes all traffic from/to internet through Internet gateway
## The route table is associated to the subnet
resource "aws_route_table" "route-table-201" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw-201.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-201-rt"
  }
}

resource "aws_route_table_association" "rt-association-subnet1-201" {
  route_table_id = aws_route_table.route-table-201.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
}

## Create a SECURITY GROUP associated to the VPC
## The SG allows all incoming PORT 80 traffic from everywhere
## The SG allows all incoming PORT 22 traffic from your IP
## The SG allows all incoming PORT ICMP (ping) traffic from your IP

resource "aws_security_group" "sg-201" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  ## ALL HTTP, SSH AND PING INCOMING TRAFFIC ENTERING THE SECURITY GROUP
  ingress {
    from_port = 22
    protocol = "tcp"
    to_port = 22
    cidr_blocks = ["${module.myip.address}/32" ]
  }
  ingress {
    from_port = 80
    protocol = "tcp"
    to_port = 80
    cidr_blocks = ["0.0.0.0/0" ]
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
    Name: "cpu-201-sg"
  }
}

## CREATE an EC2 inside the subnet (with the associated route table) and inside the security group
## As a consequence the EC2 should be reachable (ping and SSH) from internet
## And EC2 can initiate traffic to internet (curl...)
resource "aws_instance" "public-ec2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
  security_groups = [aws_security_group.sg-201.id]
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "cpu-201-ec2-1"
    Description: "EC2 in a subnet with a route and a security group (in first subnet)"
  }
}

output "cpu-201-ec2-1-public-ip" {
  value = aws_instance.public-ec2.public_ip
}
output "cpu-201-sg-id" {
  value = aws_security_group.sg-201.id
}
output "cpu-201-rt-id" {
  value = aws_route_table.route-table-201.id
}

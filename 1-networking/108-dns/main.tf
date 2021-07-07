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

data "aws_ami" "amazon-linux" {
  owners = ["amazon"]
  most_recent = true

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}
data "aws_availability_zones" "all" {}

######################################################################################
## Create two VPCs with DNS settings
## 1) create a new VPC with AWS DNS enabled
## 2) create a new VPC with AWS DNS disabled
## 3) create an EC2 in default subnet
## 4) check that EC2 has a host name

resource "aws_vpc" "net-108-vpc-1" {
  cidr_block = "10.100.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support = true

  tags = {
    Purpose: var.dojo
    Name: "net-108-vpc-1"
    Description: "A First VPC with DNS enabled in ${var.region} Region"
  }
}

resource "aws_vpc" "net-108-vpc-2" {
  cidr_block = "10.200.0.0/16"
  enable_dns_hostnames = false
  enable_dns_support = false

  tags = {
    Purpose: var.dojo
    Name: "net-108-vpc-2"
    Description: "A Second VPC with DNS disabled in ${var.region} Region"
  }
}

resource "aws_subnet" "subnet-1" {
  cidr_block = "10.100.0.0/24"
  vpc_id = aws_vpc.net-108-vpc-1.id
  availability_zone = data.aws_availability_zones.all.names[0]
  tags = {
    Purpose: var.dojo
    Name: "net-108-subnet-1"
  }
}


resource "aws_subnet" "subnet-2" {
  cidr_block = "10.200.0.0/24"
  vpc_id = aws_vpc.net-108-vpc-2.id
  availability_zone = data.aws_availability_zones.all.names[0]
  tags = {
    Purpose: var.dojo
    Name: "net-108-subnet-2"
  }
}

resource "aws_internet_gateway" "igw-108" {
  vpc_id = aws_vpc.net-108-vpc-1.id
  tags = {
    Purpose: var.dojo
    Name: "net-108-igw"
  }
}

resource "aws_route" "route-108-1-internet" {
  route_table_id = aws_vpc.net-108-vpc-1.default_route_table_id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id = aws_internet_gateway.igw-108.id
}

resource "aws_security_group_rule" "sg-1-ssh" {
  from_port = 22
  protocol = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = aws_vpc.net-108-vpc-1.default_security_group_id
  to_port = 22
  type = "ingress"
}
## CREATE an EC2 inside the  subnet
## Use default route and default security group (for this DOJO only)
## EC2 should receive a DNS names (public one and private one)
resource "aws_instance" "public-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = aws_subnet.subnet-1.id
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-108-ec2-1"
    Description: "EC2 in a subnet with VPC with DNS enabled"
  }
}

resource "aws_instance" "public-ec2-2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = aws_subnet.subnet-2.id
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-108-ec2-2"
    Description: "EC2 in a subnet with VPC with DNS disabled"
  }
}

output "net-108-ec2-1-public-ip" {
  value = aws_instance.public-ec2-1.public_ip
}

output "net-108-ec2-1-private-ip" {
  value = aws_instance.public-ec2-1.private_ip
}
output "net-108-ec2-1-public-dns" {
  value = aws_instance.public-ec2-1.public_dns
}

output "net-108-ec2-1-private-dns" {
  value = aws_instance.public-ec2-1.private_dns
}

output "net-108-ec2-2-public-ip" {
  value = aws_instance.public-ec2-2.public_ip
}

output "net-108-ec2-2-private-ip" {
  value = aws_instance.public-ec2-2.private_ip
}
output "net-108-ec2-2-public-dns" {
  value = aws_instance.public-ec2-2.public_dns
}

output "net-108-ec2-2-private-dns" {
  value = aws_instance.public-ec2-2.private_dns
}

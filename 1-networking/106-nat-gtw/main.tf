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
data "terraform_remote_state" "bastion-105" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    key = "105-bastion"
    region = var.tf-s3-region
  }
}


######################################################################################
## Add a NAT GATEWAY to allow private EC2 to initiate traffic TO internet (ONE WAY)
## 1) create a NAT Gateway in PUBLIC subnet (and an ELASTIC IP for the NAT)
## 2) modify Private Subnet Route Table to add a route to Internet through NAT Gateway
## 3) authorize outgoing internet traffic in Private SG (egress)

## Add NAT GATEWAY
resource "aws_eip" "nat-gw-eip-106" {
  tags = {
    Purpose: var.dojo
    Name: "net-106-nat-gtw-eip"
    Description: "Elastic Public IP for NAT Gateway"
  }
}
resource "aws_nat_gateway" "nat-gtw-106" {
  allocation_id = aws_eip.nat-gw-eip-106.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id

  tags = {
    Purpose: var.dojo
    Name: "net-106-nat-gtw"
    Description: "A NAT Gateway (ONE direction only) attached to VPC ${data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id} "

  }
}


## Modify the private RouteTable to route outgoing traffic TO NAT Gateway
## The route table is associated to the private subnet
resource "aws_route" "route-106-1" {
  route_table_id = data.terraform_remote_state.bastion-105.outputs.net-105-rt-2-id
  nat_gateway_id = aws_nat_gateway.nat-gtw-106.id
  destination_cidr_block = "0.0.0.0/0"
}

## Allow OUTGOING traffic in Private Security Group
resource "aws_security_group_rule" "outgoing-route-sg" {
  security_group_id = data.terraform_remote_state.bastion-105.outputs.net-105-sg-2-id
  from_port = 0
  protocol = -1
  to_port = 0
  type = "egress"
  cidr_blocks      = ["0.0.0.0/0"]
}

output "net-106-nat-gtw-eip" {
  value = aws_eip.nat-gw-eip-106.private_ip
}

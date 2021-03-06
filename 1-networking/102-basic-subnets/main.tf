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

######################################################################################
## SUBNETS
######################################################################################
## 1) A Subnet is a subset of private IPs of the VPC CIDR block
## The subnet CIDR block indicates the range of IPs that can be used in the subnet
## By definition the subnet CIDR block is included in the VPC CIDR block  (i.e. the /x of the subnet CIDR is greater than the /y of the VPC CIDR)
## 2) A Subnet lies in ONE Availability Zone (one AZ of the VPC Region AZs)
## 3) Subnets CIDR of the same VPC can't overlap

## This first SUBNET lies in the first AZ of the Region and has (256) IPs from 10.1.0.0 to 10.1.0.255
resource "aws_subnet" "subnet-1" {
  cidr_block = "10.1.0.0/24"
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  availability_zone = data.aws_availability_zones.all.names[0]
  tags = {
    Purpose: var.dojo
    Name: "net-102-subnet-1"
  }
}

## This second SUBNET lies in the second AZ of the Region and has (256) IPs from 10.1.1.0 to 10.1.1.255
resource "aws_subnet" "subnet-2" {
  cidr_block = "10.1.1.0/24"
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  availability_zone = data.aws_availability_zones.all.names[1]
  tags = {
    Purpose: var.dojo
    Name: "net-102-subnet-2"
  }
}

## This third SUBNET lies in the first AZ of the Region and has (4096) IPs from 10.1.224.0 to 10.1.239.255
resource "aws_subnet" "subnet-3" {
  cidr_block = "10.1.224.0/20"
  ## cidr_block = "10.1.1.0/24" Would conflict with previous subnet. InvalidSubnet.Conflict: The CIDR '10.1.1.0/24' conflicts with another subnet

  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  availability_zone = data.aws_availability_zones.all.names[0]
  tags = {
    Purpose: var.dojo
    Name: "net-102-subnet-3"
  }
}

## This fourth SUBNET lies in the second AZ of the Region and has (4096) IPs from 10.1.240.0 to 10.1.255.255
resource "aws_subnet" "subnet-4" {
  cidr_block = "10.1.240.0/20"
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  availability_zone = data.aws_availability_zones.all.names[1]
  tags = {
    Purpose: var.dojo
    Name: "net-102-subnet-4"
  }
}

########################################################################################################################
## OUTPUTS FOR FOLLOWING TUTORIALS
########################################################################################################################
output "net-102-subnet-1-id" {
  value = aws_subnet.subnet-1.id
}
output "net-102-subnet-2-id" {
  value = aws_subnet.subnet-2.id
}
output "net-102-subnet-3-id" {
  value = aws_subnet.subnet-3.id
}
output "net-102-subnet-4-id" {
  value = aws_subnet.subnet-4.id
}

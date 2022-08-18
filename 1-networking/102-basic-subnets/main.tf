########################################################################################################################
variable "vpc_id" {
  type = string
}

######################################################################################
## SUBNETS
######################################################################################
## 1) A Subnet is a subset of private IP addresses of the VPC CIDR block
## The subnet CIDR block indicates the range of IP addresses that can be used in the subnet
## By definition the subnet CIDR block is included in the VPC CIDR block  (i.e. the /x of the subnet CIDR is greater than the /y of the VPC CIDR)
## 2) A Subnet spans in ONE Availability Zone (one AZ of the VPC Region AZs)
## 3) Subnets CIDR of the same VPC can't overlap

## This first SUBNET lies in the first AZ of the Region and has (256) IPs from 10.1.0.0 to 10.1.0.255
resource "aws_subnet" "net-102-subnet-1" {
  cidr_block = "10.1.0.0/24"
  vpc_id = var.vpc_id
  availability_zone = data.aws_availability_zones.all.names[0]
  tags = {
    Purpose: var.dojo
    Name: "net-102-subnet-1"
  }
}

## This second SUBNET lies in the second AZ of the Region and has (256) IPs from 10.1.1.0 to 10.1.1.255
resource "aws_subnet" "net-102-subnet-2" {
  cidr_block = "10.1.1.0/24"
  vpc_id = var.vpc_id
  availability_zone = data.aws_availability_zones.all.names[1]
  tags = {
    Purpose: var.dojo
    Name: "net-102-subnet-2"
  }
}

## This third SUBNET lies in the first AZ of the Region and has (4096) IPs from 10.1.224.0 to 10.1.239.255
resource "aws_subnet" "net-102-subnet-3" {
  cidr_block = "10.1.224.0/20"
  ## cidr_block = "10.1.1.0/24" Would conflict with previous subnet. InvalidSubnet.Conflict: The CIDR '10.1.1.0/24' conflicts with another subnet

  vpc_id = var.vpc_id
  availability_zone = data.aws_availability_zones.all.names[0]
  tags = {
    Purpose: var.dojo
    Name: "net-102-subnet-3"
  }
}

## This fourth SUBNET lies in the second AZ of the Region and has (4096) IPs from 10.1.240.0 to 10.1.255.255
resource "aws_subnet" "net-102-subnet-4" {
  cidr_block = "10.1.240.0/20"
  vpc_id = var.vpc_id
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
  value = aws_subnet.net-102-subnet-1.id
}
output "net-102-subnet-2-id" {
  value = aws_subnet.net-102-subnet-2.id
}
output "net-102-subnet-3-id" {
  value = aws_subnet.net-102-subnet-3.id
}
output "net-102-subnet-4-id" {
  value = aws_subnet.net-102-subnet-4.id
}

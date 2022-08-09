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

variable "private_route_table_105_id" {
  type = string
}

variable "private_security_group_105_id" {
  type = string
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
  subnet_id = var.public_subnet_102_id

  tags = {
    Purpose: var.dojo
    Name: "net-106-nat-gtw"
    Description: "A NAT Gateway (ONE direction only) attached to VPC ${var.vpc_id} "

  }
}


## Modify the private RouteTable to route outgoing traffic TO NAT Gateway
## The route table is associated to the private subnet
resource "aws_route" "route-106-1" {
  route_table_id = var.private_route_table_105_id
  nat_gateway_id = aws_nat_gateway.nat-gtw-106.id
  destination_cidr_block = "0.0.0.0/0"
}

## Allow OUTGOING traffic in Private Security Group
resource "aws_security_group_rule" "outgoing-route-sg" {
  security_group_id = var.private_security_group_105_id
  from_port = 0
  protocol = -1
  to_port = 0
  type = "egress"
  cidr_blocks      = ["0.0.0.0/0"]
}

output "net-106-nat-gtw-eip" {
  value = aws_eip.nat-gw-eip-106.private_ip
}

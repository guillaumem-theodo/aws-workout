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
resource "aws_eip" "net-106-eip" {
  tags = {
    Purpose: var.dojo
    Name: "net-106-eip"
  }
}
resource "aws_nat_gateway" "net-106-nat-gtw" {
  allocation_id = aws_eip.net-106-eip.id
  subnet_id = var.public_subnet_102_id

  tags = {
    Purpose: var.dojo
    Name: "net-106-nat-gtw"
  }
}


## Modify the private RouteTable to route outgoing traffic TO NAT Gateway
## The route table is associated to the private subnet
resource "aws_route" "net-106-route-1" {
  route_table_id = var.private_route_table_105_id
  nat_gateway_id = aws_nat_gateway.net-106-nat-gtw.id
  destination_cidr_block = "0.0.0.0/0"
}

output "net-106-nat-gtw-eip" {
  value = aws_eip.net-106-eip.private_ip
}

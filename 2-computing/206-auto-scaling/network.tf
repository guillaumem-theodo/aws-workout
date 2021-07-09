######################################################################################
## Internet Gateway for Bastion EC2 and ALB
resource "aws_internet_gateway" "igw-206" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  tags = {
    Purpose: var.dojo
    Name: "cpu-206-igw"
  }
}

resource "aws_route_table" "route-table-206-1" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw-206.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-206-rt-1"
  }
}

resource "aws_route_table_association" "rt-association-subnet1-206" {
  route_table_id = aws_route_table.route-table-206-1.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
}
resource "aws_route_table_association" "rt-association-subnet2-206" {
  route_table_id = aws_route_table.route-table-206-1.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-2-id
}

######################################################################################
## Add NAT GATEWAY, and associate the NAT Gateway with route table for subnet 2 and 3 (dedicated to workers EC2)
resource "aws_eip" "nat-gw-eip-206" {
  tags = {
    Purpose: var.dojo
    Name: "cpu-206-nat-gtw-eip"
  }
}
resource "aws_nat_gateway" "nat-gtw-206" {
  allocation_id = aws_eip.nat-gw-eip-206.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id

  tags = {
    Purpose: var.dojo
    Name: "cpu-206-nat-gtw"
  }
}
resource "aws_route_table" "route-table-206-2" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  route {
    cidr_block = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat-gtw-206.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-206-rt-2"
  }
}
resource "aws_route_table_association" "rt-association-subnet3-206" {
  route_table_id = aws_route_table.route-table-206-2.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-3-id
}
resource "aws_route_table_association" "rt-association-subnet4-206" {
  route_table_id = aws_route_table.route-table-206-2.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-4-id
}

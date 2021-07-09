######################################################################################
## Internet Gateway for Bastion EC2 and ALB
resource "aws_internet_gateway" "igw-205" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  tags = {
    Purpose: var.dojo
    Name: "cpu-205-igw"
  }
}

resource "aws_route_table" "route-table-205-1" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw-205.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-rt-1"
  }
}

resource "aws_route_table_association" "rt-association-subnet1-205" {
  route_table_id = aws_route_table.route-table-205-1.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
}
resource "aws_route_table_association" "rt-association-subnet2-205" {
  route_table_id = aws_route_table.route-table-205-1.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-2-id
}

######################################################################################
## Add NAT GATEWAY, and associate the NAT Gateway with route table for subnet 2 and 3 (dedicated to workers EC2)
resource "aws_eip" "nat-gw-eip-205" {
  tags = {
    Purpose: var.dojo
    Name: "cpu-205-nat-gtw-eip"
  }
}
resource "aws_nat_gateway" "nat-gtw-205" {
  allocation_id = aws_eip.nat-gw-eip-205.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-nat-gtw"
  }
}
resource "aws_route_table" "route-table-205-2" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  route {
    cidr_block = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat-gtw-205.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-rt-2"
  }
}
resource "aws_route_table_association" "rt-association-subnet3-205" {
  route_table_id = aws_route_table.route-table-205-2.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-3-id
}
resource "aws_route_table_association" "rt-association-subnet4-205" {
  route_table_id = aws_route_table.route-table-205-2.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-4-id
}

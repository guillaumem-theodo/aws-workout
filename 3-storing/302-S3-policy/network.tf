######################################################################################
## Internet Gateway for EC2
resource "aws_internet_gateway" "igw-302" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  tags = {
    Purpose: var.dojo
    Name: "sto-302-igw"
  }
}

resource "aws_route_table" "route-table-302-1" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw-302.id
  }

  tags = {
    Purpose: var.dojo
    Name: "sto-302-rt-1"
  }
}

resource "aws_route_table_association" "rt-association-subnet1-302" {
  route_table_id = aws_route_table.route-table-302-1.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
}

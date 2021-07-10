######################################################################################
## NETWORK (IGW + SUBNET + ROUTE TABLE for TEST EC2)
resource "aws_internet_gateway" "igw-208" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  tags = {
    Purpose: var.dojo
    Name: "cpu-208-igw"
  }
}

resource "aws_route_table" "route-table-208-1" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw-208.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-208-rt-1"
  }
}
resource "aws_route_table_association" "rt-association-subnet1-208" {
  route_table_id = aws_route_table.route-table-208-1.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
}

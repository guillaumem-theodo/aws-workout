######################################################################################
## NETWORK (IGW + SUBNET + ROUTE TABLE for TEST EC2)
resource "aws_internet_gateway" "igw-209" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  tags = {
    Purpose: var.dojo
    Name: "cpu-209-igw"
  }
}

resource "aws_route_table" "route-table-209-1" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw-209.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-209-rt-1"
  }
}
resource "aws_route_table_association" "rt-association-subnet1-209" {
  route_table_id = aws_route_table.route-table-209-1.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
}

######################################################################################
## Private Route Table for LAMBDA
resource "aws_route_table" "route-table-209-2" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  tags = {
    Purpose: var.dojo
    Name: "cpu-209-rt-2"
  }
}
resource "aws_route_table_association" "rt-association-subnet2-209" {
  route_table_id = aws_route_table.route-table-209-2.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-3-id
}

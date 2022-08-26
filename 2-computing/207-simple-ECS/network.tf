######################################################################################
## Internet Gateway for Bastion EC2 and ALB
resource "aws_internet_gateway" "cpu-207-igw" {
  vpc_id = var.vpc_id
  tags = {
    Purpose: var.dojo
    Name: "cpu-207-igw"
  }
}

resource "aws_route_table" "cpu-207-rt-1" {
  vpc_id = var.vpc_id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.cpu-207-igw.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-207-rt-1"
  }
}

resource "aws_route_table_association" "cpu-207-rt-association-subnet1" {
  route_table_id = aws_route_table.cpu-207-rt-1.id
  subnet_id = var.subnet1_102_id
}
resource "aws_route_table_association" "rt-association-subnet2-207" {
  route_table_id = aws_route_table.cpu-207-rt-1.id
  subnet_id = var.subnet2_102_id
}

######################################################################################
## Add NAT GATEWAY, and associate the NAT Gateway with route table for subnet 2 and 3 (dedicated to workers EC2)
resource "aws_eip" "cpu-207-nat-gw-eip" {
  tags = {
    Purpose: var.dojo
    Name: "cpu-207-nat-gw-eip"
  }
}
resource "aws_nat_gateway" "cpu-207-nat-gtw" {
  allocation_id = aws_eip.cpu-207-nat-gw-eip.id
  subnet_id = var.subnet1_102_id

  tags = {
    Purpose: var.dojo
    Name: "cpu-207-nat-gtw"
  }
}
resource "aws_route_table" "cpu-207-rt-2" {
  vpc_id = var.vpc_id

  route {
    cidr_block = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.cpu-207-nat-gtw.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-207-rt-2"
  }
}
resource "aws_route_table_association" "rt-association-subnet3-207" {
  route_table_id = aws_route_table.cpu-207-rt-2.id
  subnet_id = var.subnet3_102_id
}
resource "aws_route_table_association" "rt-association-subnet4-207" {
  route_table_id = aws_route_table.cpu-207-rt-2.id
  subnet_id = var.subnet4_102_id
}

######################################################################################
## NETWORK (IGW + SUBNET + ROUTE TABLE for TEST EC2)
resource "aws_internet_gateway" "cpu-209-igw" {
  vpc_id = var.vpc_id
  tags = {
    Purpose: var.dojo
    Name: "cpu-209-igw"
  }
}

resource "aws_route_table" "cpu-209-rt-1" {
  vpc_id = var.vpc_id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.cpu-209-igw.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-209-rt-1"
  }
}
resource "aws_route_table_association" "rt-association-subnet1-209" {
  route_table_id = aws_route_table.cpu-209-rt-1.id
  subnet_id = var.subnet1_102_id
}

######################################################################################
## Private Route Table for LAMBDA
resource "aws_route_table" "cpu-209-rt-2" {
  vpc_id = var.vpc_id

  tags = {
    Purpose: var.dojo
    Name: "cpu-209-rt-2"
  }
}
resource "aws_route_table_association" "rt-association-subnet2-209" {
  route_table_id = aws_route_table.cpu-209-rt-2.id
  subnet_id = var.subnet3_102_id
}

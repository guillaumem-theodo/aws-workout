########################################################################################################################

########################################################################################################################


## Internet Gateway is a BIDIRECTIONAL gateway to Internet from VPC
resource "aws_internet_gateway" "cpu-204-igw" {
  vpc_id = var.vpc_id
  tags = {
    Purpose: var.dojo
    Name: "cpu-204-igw"
  }
}

## Create a ROUTE TABLE associated to the VPC
## The route table routes all traffic from/to internet through Internet gateway
## The route table is associated to the subnet
resource "aws_route_table" "cpu-204-rt" {
  vpc_id = var.vpc_id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.cpu-204-igw.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-204-rt"
  }
}

resource "aws_route_table_association" "cpu-204-rt-association-subnet1" {
  route_table_id = aws_route_table.cpu-204-rt.id
  subnet_id = var.subnet_102_id
}

resource "aws_security_group" "cpu-204-sg-1" {
  vpc_id = var.vpc_id

  ## ALL HTTP, SSH AND PING INCOMING TRAFFIC ENTERING THE SECURITY GROUP
  ingress {
    from_port = 22
    protocol = "tcp"
    to_port = 22
    cidr_blocks = ["${module.myip.address}/32" ]
  }
  ingress {
    from_port = 80
    protocol = "tcp"
    to_port = 80
    cidr_blocks = ["0.0.0.0/0" ]
  }
  ingress {
    from_port = -1
    protocol = "icmp"
    to_port = -1
    cidr_blocks = ["${module.myip.address}/32" ]
  }
  ## ALL OUTGOING TRAFFIC INITIATED FROM THE SECURITY GROUP
  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-204-sg-1"
  }
}


########################################################################################################################
variable "vpc_id" {
  type = string
}

variable "subnet_102_id" {
  type = string
}

######################################################################################
## Create an access FROM and TO internet on our EC2 (public EC2)
## 1) create an internet gateway (IGW)
## 2) create a route table and a route to 0.0.0.0 via IGW
## 3) authorize PING and SSH in a security group
## 4) associate the security group to the EC2 instances

## Internet Gateway is a BIDIRECTIONAL gateway to Internet from VPC
resource "aws_internet_gateway" "igw-104" {
  vpc_id = var.vpc_id
  tags = {
    Purpose: var.dojo
    Name: "net-104-igw"
    Description: "An Internet Gateway (both direction) attached to VPC ${var.vpc_id} "
  }
}

## Create a ROUTE TABLE associated to the VPC
## The route table routes all traffic from/to internet through Internet gateway
## The route table is associated to the subnet
resource "aws_route_table" "route-table-104" {
  vpc_id = var.vpc_id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw-104.id
  }

  tags = {
    Purpose: var.dojo
    Name: "net-104-rt"
  }
}

resource "aws_route_table_association" "rt-association-subnet1-104" {
  route_table_id = aws_route_table.route-table-104.id
  subnet_id =  var.subnet_102_id
}

## Create a SECURITY GROUP associated to the VPC
## The SG allows all incoming PORT 22 traffic from Internet (0.0.0.0/0) - bad habit but for demo purpose
## The SG allows all incoming PORT ICMP (ping) traffic from Internet (0.0.0.0/0) - bad habit but for demo purpose

resource "aws_security_group" "sg-104" {
  vpc_id = var.vpc_id

  ## ALL SSH AND PING INCOMING TRAFFIC ENTERING THE SECURITY GROUP
  ingress {
    from_port = 22
    protocol = "tcp"
    to_port = 22
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port = -1
    protocol = "icmp"
    to_port = -1
    cidr_blocks = ["0.0.0.0/0"]
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
    Name: "net-104-sg"
  }
}

## CREATE an EC2 inside the subnet (with the associated route table) and associated to the security group
## As a consequence the EC2 should be reachable (ping and SSH) from internet
## And EC2 can initiate traffic to internet (curl...)
resource "aws_instance" "public-ec2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = var.subnet_102_id
  security_groups = [aws_security_group.sg-104.id]
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-104-ec2-1"
    Description: "EC2 in a subnet with a route and a security group (in first subnet)"
  }
}

output "net-104-ec2-1-public-ip" {
  value = aws_instance.public-ec2.public_ip
}
output "net-104-sg-id" {
  value = aws_security_group.sg-104.id
}
output "net-104-rt-id" {
  value = aws_route_table.route-table-104.id
}

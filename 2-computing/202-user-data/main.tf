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
## 3) authorize HTTP, PING and SSH in a security group
## 4) associate the security group to the EC2 instances

## Internet Gateway is a BIDIRECTIONAL gateway to Internet from VPC
resource "aws_internet_gateway" "cpu-202-igw" {
  vpc_id = var.vpc_id
  tags = {
    Purpose: var.dojo
    Name: "cpu-202-igw"
  }
}

## Create a ROUTE TABLE associated to the VPC
## The route table routes all traffic from/to internet through Internet gateway
## The route table is associated to the subnet
resource "aws_route_table" "cpu-202-rt" {
  vpc_id = var.vpc_id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.cpu-202-igw.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-202-rt"
  }
}

resource "aws_route_table_association" "rt-association-subnet1-202" {
  route_table_id = aws_route_table.cpu-202-rt.id
  subnet_id = var.subnet_102_id
}

## Create a SECURITY GROUP associated to the VPC
## The SG allows all incoming PORT 80 traffic from everywhere
## The SG allows all incoming PORT 22 traffic from your IP
## The SG allows all incoming PORT ICMP (ping) traffic from your IP

resource "aws_security_group" "cpu-202-sg-1" {
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
    Name: "cpu-202-sg-1"
  }
}

## CREATE an EC2 inside the subnet (with the associated route table) and inside the security group
## As a consequence the EC2 should be reachable (ping and SSH) from internet
## And EC2 can initiate traffic to internet (curl...)
resource "aws_instance" "cpu-202-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = var.subnet_102_id
  vpc_security_group_ids = [aws_security_group.cpu-202-sg-1.id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-202-ec2-1"
  }
}

output "cpu-202-ec2-1-public-ip" {
  value = aws_instance.cpu-202-ec2-1.public_ip
}
output "cpu-202-sg-id" {
  value = aws_security_group.cpu-202-sg-1.id
}
output "cpu-202-rt-id" {
  value = aws_route_table.cpu-202-rt.id
}

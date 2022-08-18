########################################################################################################################

######################################################################################
## Create two VPCs with DNS settings
## 1) create a new VPC with AWS DNS enabled
## 2) create a new VPC with AWS DNS disabled
## 3) In each VPC, create an EC2
## 4) check that EC2s have (or not) a host name

resource "aws_vpc" "net-108-vpc-1" {
  cidr_block = "10.100.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support = true

  tags = {
    Purpose: var.dojo
    Name: "net-108-vpc-1"
  }
}

resource "aws_vpc" "net-108-vpc-2" {
  cidr_block = "10.200.0.0/16"
  enable_dns_hostnames = false
  enable_dns_support = false

  tags = {
    Purpose: var.dojo
    Name: "net-108-vpc-2"
  }
}

resource "aws_subnet" "net-108-subnet-1" {
  cidr_block = "10.100.0.0/24"
  vpc_id = aws_vpc.net-108-vpc-1.id
  availability_zone = data.aws_availability_zones.all.names[0]
  tags = {
    Purpose: var.dojo
    Name: "net-108-subnet-1"
  }
}


resource "aws_subnet" "net-108-subnet-2" {
  cidr_block = "10.200.0.0/24"
  vpc_id = aws_vpc.net-108-vpc-2.id
  availability_zone = data.aws_availability_zones.all.names[0]
  tags = {
    Purpose: var.dojo
    Name: "net-108-subnet-2"
  }
}

resource "aws_internet_gateway" "net-108-igw" {
  vpc_id = aws_vpc.net-108-vpc-1.id
  tags = {
    Purpose: var.dojo
    Name: "net-108-igw"
  }
}

resource "aws_route" "net-108-rt-1-internet" {
  route_table_id = aws_vpc.net-108-vpc-1.default_route_table_id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id = aws_internet_gateway.net-108-igw.id
}

resource "aws_security_group_rule" "net-108-sg-1-ssh" {
  from_port = 22
  protocol = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = aws_vpc.net-108-vpc-1.default_security_group_id
  to_port = 22
  type = "ingress"
}
## CREATE an EC2 inside the  subnet
## Use default route and default security group (for this DOJO only)
## EC2 should receive a DNS names (public one and private one)
resource "aws_instance" "net-108-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = aws_subnet.net-108-subnet-1.id
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-108-ec2-1"
  }
}

resource "aws_instance" "net-108-ec2-2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = aws_subnet.net-108-subnet-2.id
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-108-ec2-2"
  }
}

output "net-108-ec2-1-public-ip" {
  value = aws_instance.net-108-ec2-1.public_ip
}

output "net-108-ec2-1-private-ip" {
  value = aws_instance.net-108-ec2-1.private_ip
}
output "net-108-ec2-1-public-dns" {
  value = aws_instance.net-108-ec2-1.public_dns
}

output "net-108-ec2-1-private-dns" {
  value = aws_instance.net-108-ec2-1.private_dns
}

output "net-108-ec2-2-public-ip" {
  value = aws_instance.net-108-ec2-2.public_ip
}

output "net-108-ec2-2-private-ip" {
  value = aws_instance.net-108-ec2-2.private_ip
}
output "net-108-ec2-2-public-dns" {
  value = aws_instance.net-108-ec2-2.public_dns
}

output "net-108-ec2-2-private-dns" {
  value = aws_instance.net-108-ec2-2.private_dns
}

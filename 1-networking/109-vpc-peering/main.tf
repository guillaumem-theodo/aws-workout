########################################################################################################################

######################################################################################
## Create four VPCs with DNS settings
## 1) create three new  VPC with AWS DNS enabled
## 2) create EC2 in each VPC
## 3) change routes in route tables
## 4) test visibility of EC2 from each other
## 5) check transitivity (VPC peering IS NOT transitive)

resource "aws_vpc" "net-109-vpc-1" {
  cidr_block = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support = true

  tags = {
    Purpose: var.dojo
    Name: "net-109-vpc-1"
    Description: "A First VPC in ${var.region} Region"
  }
}

resource "aws_vpc" "net-109-vpc-2" {
  cidr_block = "10.1.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support = true

  tags = {
    Purpose: var.dojo
    Name: "net-109-vpc-2"
    Description: "A Second VPC in ${var.region} Region"
  }
}

resource "aws_vpc" "net-109-vpc-3" {
  cidr_block = "10.2.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support = true

  tags = {
    Purpose: var.dojo
    Name: "net-109-vpc-3"
    Description: "A Third VPC in ${var.region} Region"
  }
}

resource "aws_subnet" "subnet-1" {
  cidr_block = "10.0.0.0/24"
  vpc_id = aws_vpc.net-109-vpc-1.id
  availability_zone = data.aws_availability_zones.all.names[0]
  tags = {
    Purpose: var.dojo
    Name: "net-109-subnet-1"
  }
}

resource "aws_subnet" "subnet-2" {
  cidr_block = "10.1.0.0/24"
  vpc_id = aws_vpc.net-109-vpc-2.id
  availability_zone = data.aws_availability_zones.all.names[0]
  tags = {
    Purpose: var.dojo
    Name: "net-109-subnet-2"
  }
}

resource "aws_subnet" "subnet-3" {
  cidr_block = "10.2.0.0/24"
  vpc_id = aws_vpc.net-109-vpc-3.id
  availability_zone = data.aws_availability_zones.all.names[0]
  tags = {
    Purpose: var.dojo
    Name: "net-109-subnet-3"
  }
}

resource "aws_internet_gateway" "igw-109" {
  vpc_id = aws_vpc.net-109-vpc-3.id
  tags = {
    Purpose: var.dojo
    Name: "net-109-igw"
  }
}

resource "aws_route" "route-109-3-internet" {
  route_table_id = aws_vpc.net-109-vpc-3.default_route_table_id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id = aws_internet_gateway.igw-109.id
}

resource "aws_security_group_rule" "sg-3-ssh" {
  from_port = 22
  protocol = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = aws_vpc.net-109-vpc-3.default_security_group_id
  to_port = 22
  type = "ingress"
}

## CREATE  EC2s inside the subnets
## Use default route and default security group (for this DOJO only)
resource "aws_instance" "public-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = aws_subnet.subnet-1.id
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-109-ec2-1"
  }
}

resource "aws_instance" "public-ec2-2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = aws_subnet.subnet-2.id
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-109-ec2-2"
  }
}

resource "aws_instance" "public-ec2-3" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = aws_subnet.subnet-3.id
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-109-ec2-3"
  }
}

## PEER VPC 3 and VPC 2
resource "aws_vpc_peering_connection" "net-109-peering-3-2" {
  vpc_id = aws_vpc.net-109-vpc-3.id
  peer_vpc_id = aws_vpc.net-109-vpc-2.id
  auto_accept = true
}

resource "aws_route" "route-109-3-2" {
  route_table_id = aws_vpc.net-109-vpc-3.default_route_table_id
  destination_cidr_block = aws_vpc.net-109-vpc-2.cidr_block
  vpc_peering_connection_id = aws_vpc_peering_connection.net-109-peering-3-2.id
}

resource "aws_route" "route-109-2-3" {
  route_table_id = aws_vpc.net-109-vpc-2.default_route_table_id
  destination_cidr_block = aws_vpc.net-109-vpc-3.cidr_block
  vpc_peering_connection_id = aws_vpc_peering_connection.net-109-peering-3-2.id
}

resource "aws_security_group_rule" "sg-3-2" {
  from_port = 0
  protocol = "-1"
  security_group_id = aws_vpc.net-109-vpc-3.default_security_group_id
  cidr_blocks = [aws_vpc.net-109-vpc-2.cidr_block]
  to_port = 0
  type = "ingress"
}

resource "aws_security_group_rule" "sg-2-3" {
  depends_on = [aws_vpc.net-109-vpc-2, aws_vpc.net-109-vpc-3]
  from_port = 0
  protocol = "-1"
  security_group_id = aws_vpc.net-109-vpc-2.default_security_group_id
  cidr_blocks = [aws_vpc.net-109-vpc-3.cidr_block]
  to_port = 0
  type = "ingress"
}

## PEER VPC 2 and VPC 1
resource "aws_vpc_peering_connection" "net-109-peering-2-1" {
  vpc_id = aws_vpc.net-109-vpc-2.id
  peer_vpc_id = aws_vpc.net-109-vpc-1.id
  auto_accept = true

  accepter {
    allow_remote_vpc_dns_resolution = true
  }

  requester {
    allow_remote_vpc_dns_resolution = true
  }

}

resource "aws_route" "route-109-2-1" {
  route_table_id = aws_vpc.net-109-vpc-2.default_route_table_id
  destination_cidr_block = aws_vpc.net-109-vpc-1.cidr_block
  vpc_peering_connection_id = aws_vpc_peering_connection.net-109-peering-2-1.id
}

resource "aws_route" "route-109-1-2" {
  route_table_id = aws_vpc.net-109-vpc-1.default_route_table_id
  destination_cidr_block = aws_vpc.net-109-vpc-2.cidr_block
  vpc_peering_connection_id = aws_vpc_peering_connection.net-109-peering-2-1.id
}

resource "aws_security_group_rule" "sg-1-2" {
  depends_on = [aws_vpc.net-109-vpc-1, aws_vpc.net-109-vpc-2]
  from_port = 0
  protocol = "-1"
  security_group_id = aws_vpc.net-109-vpc-1.default_security_group_id
  cidr_blocks = [aws_vpc.net-109-vpc-2.cidr_block]
  to_port = 0
  type = "ingress"
}

output "net-109-ec2-1-public-ip" {
  value = aws_instance.public-ec2-1.public_ip
}

output "net-109-ec2-1-private-ip" {
  value = aws_instance.public-ec2-1.private_ip
}

output "net-109-ec2-1-public-dns" {
  value = aws_instance.public-ec2-1.public_dns
}

output "net-109-ec2-1-private-dns" {
  value = aws_instance.public-ec2-1.private_dns
}

output "net-109-ec2-2-public-ip" {
  value = aws_instance.public-ec2-2.public_ip
}

output "net-109-ec2-2-private-ip" {
  value = aws_instance.public-ec2-2.private_ip
}

output "net-109-ec2-2-public-dns" {
  value = aws_instance.public-ec2-2.public_dns
}

output "net-109-ec2-2-private-dns" {
  value = aws_instance.public-ec2-2.private_dns
}

output "net-109-ec2-3-public-ip" {
  value = aws_instance.public-ec2-3.public_ip
}

output "net-109-ec2-3-private-ip" {
  value = aws_instance.public-ec2-3.private_ip
}

output "net-109-ec2-3-public-dns" {
  value = aws_instance.public-ec2-3.public_dns
}

output "net-109-ec2-3-private-dns" {
  value = aws_instance.public-ec2-3.private_dns
}

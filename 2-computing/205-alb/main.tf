## USE S3 BUCKET TO STORE TERRAFORM STATE
terraform {
  backend "s3" {
  }
}

########################################################################################################################
## INPUTS
########################################################################################################################
## NAME OF THE TUTORIAL
variable "dojo" {
  type = string
  default = "aws-workout"
}
## REGION WHERE THE AWS COMPONENTS WILL BE DEPLOYED
variable "region" {
  type = string
  default = "eu-west-2"
}

## REGION OF THE S3 BUCKET USED TO STORE TERRAFORM STATES
variable "tf-s3-region" {
  type = string
  default = "eu-west-2"
}

## NAME OF THE S3 BUCKET USED TO STORE TERRAFORM STATES
variable "tf-s3-bucket" {
  type = string
}

########################################################################################################################
provider "aws" {
  region = var.region
  profile = "aws-workout"
}

data "aws_ami" "amazon-linux" {
  owners = ["amazon"]
  most_recent = true

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}

data "terraform_remote_state" "vpc-101" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    region = var.tf-s3-region
    key = "101-basic-vpc"
  }
}

data "terraform_remote_state" "subnets-102" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    key = "102-basic-subnets"
    region = var.tf-s3-region
  }
}
######################################################################################
#### RETRIEVE MY IP for the BASTION SG
######################################################################################
module myip {
  source  = "4ops/myip/http"
  version = "1.0.0"
}

######################################################################################
## CREATE A PUBLIC SECURITY GROUP, for ALB
## - Allowing all HTTP traffic from Internet --> Needed to receive requests from internet
## - Allowing all traffic TO everywhere --> Needed to be able to forward calls to WORKERS
resource "aws_security_group" "sg-205-public" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  ingress {
    from_port = 80
    protocol = "tcp"
    to_port = 80
    cidr_blocks = ["0.0.0.0/0" ]
  }
  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-sg-1"
    Description: "Security Group for ALB"
  }
}

######################################################################################
## CREATE A BASTION SECURITY GROUP Allowing all SSH traffic from myIP
resource "aws_security_group" "sg-205-bastion" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  ingress {
    from_port = 22
    protocol = "tcp"
    to_port = 22
    cidr_blocks = ["${module.myip.address}/32" ]
  }
  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }
  tags = {
    Purpose: var.dojo
    Name: "cpu-205-sg-2"
    Description: "Security Group for Bastion EC2"
  }
}

######################################################################################
## CREATE A PRIVATE SECURITY GROUP
## - Allowing all HTTP traffic from Previous Security Group
## - Allowing all outgoing traffic to internet --> Needed for Yum Update, Yum Install
resource "aws_security_group" "sg-205-private" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  ingress {
    from_port = 80
    protocol = "tcp"
    to_port = 80
    security_groups = [aws_security_group.sg-205-public.id]
  }
  ingress {
    from_port = 22
    protocol = "tcp"
    to_port = 22
    security_groups = [aws_security_group.sg-205-bastion.id]
  }
  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-sg-3"
    Description: "Security Group for Workers"
  }
}

######################################################################################
## Internet Gateway for Bastion and ALB
resource "aws_internet_gateway" "igw-205" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  tags = {
    Purpose: var.dojo
    Name: "cpu-205-igw"
  }
}

resource "aws_route_table" "route-table-205-1" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw-205.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-rt-1"
  }
}

resource "aws_route_table_association" "rt-association-subnet1-205" {
  route_table_id = aws_route_table.route-table-205-1.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
}
resource "aws_route_table_association" "rt-association-subnet2-205" {
  route_table_id = aws_route_table.route-table-205-1.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-2-id
}

######################################################################################
## Add NAT GATEWAY, and associate the NAT Gateway with route table for subnet 2 and 3 (workers)
resource "aws_eip" "nat-gw-eip-205" {
  tags = {
    Purpose: var.dojo
    Name: "cpu-205-nat-gtw-eip"
  }
}
resource "aws_nat_gateway" "nat-gtw-205" {
  allocation_id = aws_eip.nat-gw-eip-205.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-nat-gtw"
  }
}
resource "aws_route_table" "route-table-205-2" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  route {
    cidr_block = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat-gtw-205.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-rt-2"
  }
}
resource "aws_route_table_association" "rt-association-subnet3-205" {
  route_table_id = aws_route_table.route-table-205-2.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-3-id
}
resource "aws_route_table_association" "rt-association-subnet4-205" {
  route_table_id = aws_route_table.route-table-205-2.id
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-4-id
}

######################################################################################
## WORKERS
resource "aws_instance" "worker-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = false
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-3-id
  security_groups = [aws_security_group.sg-205-private.id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-ec2-1"
  }
}

// LAUNCH THREE WORKER EC2 (each with meta-data and HTTP server)

resource "aws_instance" "worker-ec2-2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = false
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-3-id
  security_groups = [aws_security_group.sg-205-private.id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-ec2-2"
  }
}

resource "aws_instance" "worker-ec2-3" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = false
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-4-id
  security_groups = [aws_security_group.sg-205-private.id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-ec2-3"
  }
}

######################################################################################
## BASTION EC2
resource "aws_instance" "bastion-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
  security_groups = [aws_security_group.sg-205-bastion.id]
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-ec2-bastion-1"
  }
}

######################################################################################
## ALB
resource "aws_lb" "my_alb" {
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.sg-205-public.id]
  subnets            = [
    data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id,
    data.terraform_remote_state.subnets-102.outputs.net-102-subnet-2-id
  ]

  enable_deletion_protection = false
  name = "cpu-205-alb"

  tags = {
    Purpose: "demo"
    Name: "cpu-205-alb"
  }
}

resource "aws_lb_target_group" "my_alb_target_group" {
  port     = 80
  protocol = "HTTP"
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
}

resource "aws_lb_target_group_attachment" "target_1" {
  target_group_arn = aws_lb_target_group.my_alb_target_group.arn
  target_id        = aws_instance.worker-ec2-1.id
  port             = 80
}

resource "aws_lb_target_group_attachment" "target_2" {
  target_group_arn = aws_lb_target_group.my_alb_target_group.arn
  target_id        = aws_instance.worker-ec2-2.id
  port             = 80
}
resource "aws_lb_target_group_attachment" "target_3" {
  target_group_arn = aws_lb_target_group.my_alb_target_group.arn
  target_id        = aws_instance.worker-ec2-3.id
  port             = 80
}

resource "aws_lb_listener" "my_alb_listener" {
  load_balancer_arn = aws_lb.my_alb.arn
  port = 80
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.my_alb_target_group.arn
  }
}


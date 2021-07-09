########################################################################################################################
provider "aws" {
  region = var.region
  profile = "aws-workout"
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
## BASTION EC2
resource "aws_instance" "bastion-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
  security_groups = [aws_security_group.sg-206-bastion.id]
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "cpu-206-ec2-bastion-1"
  }
}

######################################################################################
## ASG DEFINITION
resource "aws_autoscaling_group" "asg-206" {
  availability_zones = [data.aws_availability_zones.all.names[0]]
  desired_capacity   = 3
  max_size           = 4
  min_size           = 2

  launch_template {
    id      = aws_launch_template.launch-template-206.id
    version = "$Latest"
  }
}

######################################################################################
## ALB
## Internet Facing (internal=false)
## with one TargetGroup grouping the ASG definition
## with one ListerRule that forward all HTTP traffic to the TargetGroup

resource "aws_lb" "my_alb" {
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.sg-206-public.id]
  subnets            = [
    data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id,
    data.terraform_remote_state.subnets-102.outputs.net-102-subnet-2-id
  ]

  enable_deletion_protection = false
  name = "cpu-206-alb"

  tags = {
    Purpose: "demo"
    Name: "cpu-206-alb"
  }
}

resource "aws_lb_target_group" "my_alb_target_group" {
  port     = 80
  protocol = "HTTP"
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
}

resource "aws_autoscaling_attachment" "asg_attachment_to_alb" {
  autoscaling_group_name = aws_autoscaling_group.asg-206.id
  alb_target_group_arn   = aws_lb_target_group.my_alb_target_group.arn
}

resource "aws_lb_listener" "my_alb_listener" {
  load_balancer_arn = aws_lb.my_alb.arn
  port = 80
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.my_alb_target_group.arn
  }
}


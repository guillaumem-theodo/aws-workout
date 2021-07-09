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
## ALB
## Internet Facing (internal=false)
## with one TargetGroup grouping the ASG definition
## with one ListerRule that forward all HTTP traffic to the TargetGroup

resource "aws_lb" "my_alb" {
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.sg-207-public.id]
  subnets            = [
    data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id,
    data.terraform_remote_state.subnets-102.outputs.net-102-subnet-2-id
  ]

  enable_deletion_protection = false
  name = "cpu-207-alb"

  tags = {
    Purpose: var.dojo
    Name: "cpu-207-alb"
  }
}

resource "aws_lb_target_group" "my_alb_target_group" {
  port     = 80
  protocol = "HTTP"
  target_type = "ip" ## Needed for FARGATE awsvpc network mode
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
}

resource "aws_lb_listener" "my_alb_listener" {
  load_balancer_arn = aws_lb.my_alb.arn
  port = 80
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.my_alb_target_group.arn
  }
}



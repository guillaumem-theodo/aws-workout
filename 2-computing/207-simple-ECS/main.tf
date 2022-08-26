########################################################################################################################
variable "vpc_id" {
  type = string
}

variable "subnet1_102_id" {
  type = string
}
variable "subnet2_102_id" {
  type = string
}
variable "subnet3_102_id" {
  type = string
}
variable "subnet4_102_id" {
  type = string
}

######################################################################################
## ALB
## Internet Facing (internal=false)
## with one TargetGroup grouping the ASG definition
## with one ListerRule that forward all HTTP traffic to the TargetGroup

resource "aws_lb" "cpu-207-alb" {
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.cpu-207-sg-1-public.id]
  subnets            = [
    var.subnet1_102_id,
    var.subnet2_102_id
  ]

  enable_deletion_protection = false
  name = "cpu-207-alb"

  tags = {
    Purpose: var.dojo
    Name: "cpu-207-alb"
  }
}

resource "aws_lb_target_group" "cpu-207-alb-target-group" {
  port     = 80
  protocol = "HTTP"
  target_type = "ip" ## Needed for FARGATE awsvpc network mode
  vpc_id = var.vpc_id
}

resource "aws_lb_listener" "cpu-207-alb-listener" {
  load_balancer_arn = aws_lb.cpu-207-alb.arn
  port = 80
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.cpu-207-alb-target-group.arn
  }
}



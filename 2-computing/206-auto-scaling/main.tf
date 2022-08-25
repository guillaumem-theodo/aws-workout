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
## BASTION EC2
resource "aws_instance" "cpu-206-ec2-bastion-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = var.subnet1_102_id
  vpc_security_group_ids = [aws_security_group.cpu-206-sg-2-bastion.id]
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "cpu-206-ec2-bastion-1"
  }
}

######################################################################################
## ASG DEFINITION
resource "aws_autoscaling_group" "cpu-206-asg" {
  availability_zones = [data.aws_availability_zones.all.names[0]]
  desired_capacity   = 3
  max_size           = 4
  min_size           = 2

  launch_template {
    id      = aws_launch_template.cpu-206-launch-template.id
    version = "$Latest"
  }
}

######################################################################################
## ALB
## Internet Facing (internal=false)
## with one TargetGroup grouping the ASG definition
## with one ListerRule that forward all HTTP traffic to the TargetGroup

resource "aws_lb" "cpu-206-alb" {
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.cpu-206-sg-1-public.id]
  subnets            = [
    var.subnet1_102_id,
    var.subnet2_102_id
  ]

  enable_deletion_protection = false
  name = "cpu-206-alb"

  tags = {
    Purpose: var.dojo
    Name: "cpu-206-alb"
  }
}

resource "aws_lb_target_group" "cpu-206-alb_target_group" {
  port     = 80
  protocol = "HTTP"
  vpc_id = var.vpc_id
  tags = {
    Purpose: var.dojo
    Name: "cpu-206-alb_target_group"
  }
}

resource "aws_autoscaling_attachment" "cpu-206-asg_attachment_to_alb" {
  autoscaling_group_name = aws_autoscaling_group.cpu-206-asg.id
  lb_target_group_arn = aws_lb_target_group.cpu-206-alb_target_group.arn
}

resource "aws_lb_listener" "cpu-206_alb_listener" {
  load_balancer_arn = aws_lb.cpu-206-alb.arn
  port = 80
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.cpu-206-alb_target_group.arn
  }
}


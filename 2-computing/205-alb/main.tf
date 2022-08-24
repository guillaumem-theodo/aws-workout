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
resource "aws_instance" "cpu-205-ec2-bastion-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = var.subnet2_102_id
  vpc_security_group_ids = [aws_security_group.cpu-205-sg-2.id]
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-ec2-bastion-1"
  }
}

######################################################################################
## ALB
## Internet Facing (internal=false)
## with one TargetGroup grouping the 3 workers
## with one ListerRule that forward all HTTP traffic to the TargetGroup (with the 3 targets)

resource "aws_lb" "cpu-205-alb" {
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.cpu-205-sg-1.id]
  subnets            = [
    var.subnet1_102_id,
    var.subnet2_102_id,
  ]

  enable_deletion_protection = false
  name = "cpu-205-alb"

  tags = {
    Purpose: "demo"
    Name: "cpu-205-alb"
  }
}

resource "aws_lb_target_group" "cpu-205-alb-target-group" {
  port     = 80
  protocol = "HTTP"
  vpc_id = var.vpc_id
}

resource "aws_lb_target_group_attachment" "cpu-205-alb-target-1" {
  target_group_arn = aws_lb_target_group.cpu-205-alb-target-group.arn
  target_id        = aws_instance.cpu-205-ec2-worker-1.id
  port             = 80
}

resource "aws_lb_target_group_attachment" "cpu-205-alb-target-2" {
  target_group_arn = aws_lb_target_group.cpu-205-alb-target-group.arn
  target_id        = aws_instance.cpu-205-ec2-worker-2.id
  port             = 80
}
resource "aws_lb_target_group_attachment" "cpu-205-alb-target-3" {
  target_group_arn = aws_lb_target_group.cpu-205-alb-target-group.arn
  target_id        = aws_instance.cpu-205-ec2-worker-3.id
  port             = 80
}

resource "aws_lb_listener" "my_alb_listener" {
  load_balancer_arn = aws_lb.cpu-205-alb.arn
  port = 80
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.cpu-205-alb-target-group.arn
  }
}


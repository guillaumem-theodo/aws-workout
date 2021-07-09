######################################################################################
## CREATE A PUBLIC SECURITY GROUP, for ALB
## - Allowing all HTTP traffic from Internet --> Needed to receive requests from internet
## - Allowing all traffic TO everywhere --> Needed to be able to forward calls to WORKERS
resource "aws_security_group" "sg-207-public" {
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
    Name: "cpu-207-sg-1"
    Description: "Security Group for ALB"
  }
}

resource "aws_security_group" "sg-207-private" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  ingress {
    from_port = 80
    protocol = "tcp"
    to_port = 80
    security_groups = [aws_security_group.sg-207-public.id]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-207-sg-2"
    Description: "Security Group for ECS Tasks"
  }
}

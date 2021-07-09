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

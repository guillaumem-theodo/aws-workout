######################################################################################
## CREATE A PUBLIC SECURITY GROUP, for EC2
## - Allowing to SSH
## - Allowing all traffic TO everywhere
resource "aws_security_group" "sg-302-public" {
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
    Name: "sto-302-sg-1"
    Description: "Security Group for EC2"
  }
}

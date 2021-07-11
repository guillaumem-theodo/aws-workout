########################################################################################################################
resource "aws_security_group" "sg-209-public" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  ingress {
    from_port = 22
    protocol = "tcp"
    to_port = 22
    cidr_blocks = ["${module.myip.address}/32" ]
  }
  ingress {
    from_port = -1
    protocol = "icmp"
    to_port = -1
    cidr_blocks = ["${module.myip.address}/32" ]
  }
  ## ALL OUTGOING TRAFFIC INITIATED FROM THE SECURITY GROUP
  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-209-sg-1"
    Description: "Security Group for Test EC2"
  }
}

########################################################################################################################
resource "aws_security_group" "sg-209-private" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id

  ## ALL OUTGOING TRAFFIC INITIATED FROM THE SECURITY GROUP
  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-209-sg-2"
    Description: "Security Group for Lambda"
  }
}

########################################################################################################################
variable "vpc_id" {
  type = string
}

variable "subnet_102_id" {
  type = string
}
########################################################################################################################

resource "aws_iam_role" "cpu-204-iam-role-1" {
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF

  tags = {
    Purpose: var.dojo
    Name: "cpu-204-iam-role-1"
  }
}

resource "aws_iam_policy_attachment" "s3-policy-attached-to-role" {
  name = "cpu-204-iam-role-1-policy-attachment"
  roles      = [aws_iam_role.cpu-204-iam-role-1.id]
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

resource "aws_iam_instance_profile" "cpu-204-instance-profile-1" {
  role = aws_iam_role.cpu-204-iam-role-1.id
  name = "cpu-204-instance-profile-1"
  tags = {
    Purpose: var.dojo
    Name: "cpu-204-instance-profile-1"
  }
}

## Internet Gateway is a BIDIRECTIONAL gateway to Internet from VPC
resource "aws_internet_gateway" "cpu-204-igw" {
  vpc_id = var.vpc_id
  tags = {
    Purpose: var.dojo
    Name: "cpu-204-igw"
  }
}

## Create a ROUTE TABLE associated to the VPC
## The route table routes all traffic from/to internet through Internet gateway
## The route table is associated to the subnet
resource "aws_route_table" "cpu-204-rt" {
  vpc_id = var.vpc_id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.cpu-204-igw.id
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-204-rt"
  }
}

resource "aws_route_table_association" "cpu-204-rt-association-subnet1" {
  route_table_id = aws_route_table.cpu-204-rt.id
  subnet_id = var.subnet_102_id
}

resource "aws_security_group" "cpu-204-sg-1" {
  vpc_id = var.vpc_id

  ## ALL HTTP, SSH AND PING INCOMING TRAFFIC ENTERING THE SECURITY GROUP
  ingress {
    from_port = 22
    protocol = "tcp"
    to_port = 22
    cidr_blocks = ["${module.myip.address}/32" ]
  }
  ingress {
    from_port = 80
    protocol = "tcp"
    to_port = 80
    cidr_blocks = ["0.0.0.0/0" ]
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
    Name: "cpu-204-sg-1"
  }
}

resource "aws_instance" "cpu-204-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = var.subnet_102_id
  vpc_security_group_ids = [aws_security_group.cpu-204-sg-1.id]
  key_name = "aws-workout-key"
  iam_instance_profile = aws_iam_instance_profile.cpu-204-instance-profile-1.id

  tags = {
    Purpose: var.dojo
    Name: "cpu-204-ec2-1"
  }
}

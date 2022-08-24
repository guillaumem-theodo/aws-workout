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

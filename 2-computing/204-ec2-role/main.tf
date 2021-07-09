########################################################################################################################
provider "aws" {
  region = var.region
  profile = "aws-workout"
}

data "aws_iam_policy" "S2ReadOnlyAccess" {
  arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

data "terraform_remote_state" "subnets-102" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    key = "102-basic-subnets"
    region = var.tf-s3-region
  }
}

data "terraform_remote_state" "cpu-202" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    key = "202-user-data"
    region = var.tf-s3-region
  }
}

resource "aws_iam_role" "iam-role-204" {
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
    Description: "A role for the EC2 that allows the EC2 to assume some roles on your behalf"
  }
}

resource "aws_iam_policy_attachment" "s3-policy-attached-to-role" {
  name = "cpu-204-iam-role-1-policy-attachment"
  roles      = [aws_iam_role.iam-role-204.id]
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

resource "aws_iam_instance_profile" "instance-profile" {
  role = aws_iam_role.iam-role-204.id
  name = "cpu-204-instance-profile-1"
  tags = {
    Purpose: var.dojo
    Name: "cpu-204-instance-profile-1"
  }
}

resource "aws_instance" "public-ec2-2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
  security_groups = [data.terraform_remote_state.cpu-202.outputs.cpu-202-sg-id]
  key_name = "aws-workout-key"
  iam_instance_profile = aws_iam_instance_profile.instance-profile.id

  tags = {
    Purpose: var.dojo
    Name: "cpu-204-ec2-1"
    Description: "EC2 that can perform actions (read-only S3 actions) on your behalf"
  }
}

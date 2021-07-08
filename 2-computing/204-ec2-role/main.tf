## USE S3 BUCKET TO STORE TERRAFORM STATE
terraform {
  backend "s3" {
  }
}

########################################################################################################################
## INPUTS
########################################################################################################################
## NAME OF THE TUTORIAL
variable "dojo" {
  type = string
  default = "aws-workout"
}
## REGION WHERE THE AWS COMPONENTS WILL BE DEPLOYED
variable "region" {
  type = string
  default = "eu-west-2"
}

## REGION OF THE S3 BUCKET USED TO STORE TERRAFORM STATES
variable "tf-s3-region" {
  type = string
  default = "eu-west-2"
}

## NAME OF THE S3 BUCKET USED TO STORE TERRAFORM STATES
variable "tf-s3-bucket" {
  type = string
}

########################################################################################################################
provider "aws" {
  region = var.region
  profile = "aws-workout"
}

data "aws_ami" "amazon-linux" {
  owners = ["amazon"]
  most_recent = true

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
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

data "terraform_remote_state" "cpu-203" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    key = "203-meta-data"
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
    Description: "A role for the EC2"
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
  security_groups = [data.terraform_remote_state.cpu-203.outputs.cpu-203-sg-id]
  key_name = "aws-workout-key"
  iam_instance_profile = aws_iam_instance_profile.instance-profile.id

  tags = {
    Purpose: var.dojo
    Name: "cpu-204-ec2-1"
    Description: "EC2 in a subnet with a route and a security group (in first subnet)"
  }
}

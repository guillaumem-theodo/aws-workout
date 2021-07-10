########################################################################################################################
provider "aws" {
  region = var.region
  profile = "aws-workout"
}

data "terraform_remote_state" "vpc-101" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    region = var.tf-s3-region
    key = "101-basic-vpc"
  }
}

data "terraform_remote_state" "subnets-102" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    key = "102-basic-subnets"
    region = var.tf-s3-region
  }
}

########################################################################################################################
## ALLOW 'TEST' EC2 to use Read-only S3 actions
########################################################################################################################

resource "aws_iam_role" "iam-role-208" {
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
    Name: "cpu-208-iam-role-1"
    Description: "A role for the EC2 that allows the EC2 to assume some roles on your behalf"
  }
}

resource "aws_iam_policy_attachment" "s3-policy-attached-to-role" {
  name = "cpu-204-iam-role-1-policy-attachment"
  roles      = [aws_iam_role.iam-role-208.id]
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

resource "aws_iam_instance_profile" "instance-profile" {
  role = aws_iam_role.iam-role-208.id
  name = "cpu-208-instance-profile-1"
  tags = {
    Purpose: var.dojo
    Name: "cpu-208-instance-profile-1"
  }
}

########################################################################################################################
## TEST EC2

resource "aws_instance" "test-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
  security_groups = [aws_security_group.sg-208-public.id]
  key_name = "aws-workout-key"
  iam_instance_profile = aws_iam_instance_profile.instance-profile.id

  tags = {
    Purpose: var.dojo
    Name: "cpu-208-ec2-test-1"
  }
}

########################################################################################################################
resource "null_resource" "deploy-sls" {
  provisioner "local-exec" {
    command = "(cd sls; yarn; BUCKET_NAME=${aws_s3_bucket.s3-bucket-1-208.bucket} yarn deploy)"
  }

  provisioner "local-exec" {
    when    = destroy
    command = "(cd sls; serverless remove)"
  }
}


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
variable "s3_bucket1" {
  type = string
}
variable "s3_bucket2" {
  type = string
}

########################################################################################################################
## ALLOW 'TEST' EC2 to use Read-only S3 actions
########################################################################################################################

resource "aws_iam_role" "cpu-208-iam-role-1" {
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
  }
}

resource "aws_iam_policy_attachment" "cpu-208-policy-attached-to-role" {
  name = "cpu-208-policy-attached-to-role"
  roles      = [aws_iam_role.cpu-208-iam-role-1.id]
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

resource "aws_iam_instance_profile" "cpu-208-instance-profile-1" {
  role = aws_iam_role.cpu-208-iam-role-1.id
  name = "cpu-208-instance-profile-1"
  tags = {
    Purpose: var.dojo
    Name: "cpu-208-instance-profile-1"
  }
}

########################################################################################################################
## TEST EC2

resource "aws_instance" "cpu-208-ec2-test-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = var.subnet1_102_id
  vpc_security_group_ids = [aws_security_group.cpu-208-sg-1.id]
  key_name = "aws-workout-key"
  iam_instance_profile = aws_iam_instance_profile.cpu-208-instance-profile-1.id

  tags = {
    Purpose: var.dojo
    Name: "cpu-208-ec2-test-1"
  }
}

########################################################################################################################
resource "null_resource" "deploy-sls" {
  provisioner "local-exec" {
    command = "(cd sls; yarn; BUCKET_NAME=${var.s3_bucket1} yarn deploy)"
  }

  provisioner "local-exec" {
    when    = destroy
    command = "(cd sls; serverless remove)"
  }
}


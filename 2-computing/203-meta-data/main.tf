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
data "terraform_remote_state" "user-data-202" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    region = var.tf-s3-region
    key = "202-user-data"
  }
}

resource "aws_s3_bucket" "s3-bucket-1-203" {
  bucket = "unique-name-s3-bucket-1-203"   ## Change Unique Name
  tags = {
    Purpose: var.dojo
    Name: "cpu-203-s3-bucket-1"
    Description: "First Bucket for DOJO 203"
  }
}

resource "aws_s3_bucket_object" "s3-bucket-object-1-203" {
  bucket = aws_s3_bucket.s3-bucket-1-203.bucket
  key    = "a_file_uploaded_in_bucket"
  source = "README.md"
  etag = filemd5("README.md")
}

## CREATE an EC2 inside the subnet (with the associated route table) and inside the security group
## As a consequence the EC2 should be reachable (ping and SSH) from internet
## And EC2 can initiate traffic to internet (curl...)
resource "aws_instance" "public-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
  security_groups = [data.terraform_remote_state.user-data-202.outputs.cpu-202-sg-id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-203-ec2-1"
    Description: "EC2 with User-Data THAT uses Meta-Data"
  }
}

output "cpu-203-ec2-1-public-ip" {
  value = aws_instance.public-ec2-1.public_ip
}
output "cpu-203-ec2-1-id" {
  value = aws_instance.public-ec2-1.id
}
output "cpu-203-s3-arn" {
  value = aws_s3_bucket.s3-bucket-1-203.arn
}

########################################################################################################################
provider "aws" {
  region = var.region
  profile = "aws-workout"
}

data "terraform_remote_state" "vpc-101" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    key = "101-basic-vpc"
    region = var.tf-s3-region
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

######################################################################################
## CREATES TWO EC2 in subnets to show Default Routes
######################################################################################
resource "aws_instance" "ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-103-ec2-1"
    Description: "EC2 for Default Route and Default Security Group Demo Purpose (in first subnet)"
  }
}


########################################################################################################################
## OUTPUTS FOR FOLLOWING TUTORIALS
########################################################################################################################
output "net-103-ec2-1-id" {
  value = aws_instance.ec2-1.id
}
output "net-103-ec2-1-public-ip" {
  value = aws_instance.ec2-1.public_ip
}

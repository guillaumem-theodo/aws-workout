
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

## ANOTHER REGION WHERE THE AWS COMPONENTS WILL BE DEPLOYED if TWO REGIONS ARE REQUIRED
variable "another-region" {
  type = string
  default = "us-east-1"
}

## A UNIQUE KEY used for S3 buckets uniqueness
variable "unique-key" {
  type = string
}

data "aws_availability_zones" "all" {}

data "aws_ami" "amazon-linux" {
  owners = ["amazon"]
  most_recent = true

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}

######################################################################################
#### RETRIEVE MY IP for the BASTION SG
######################################################################################
module myip {
  source  = "4ops/myip/http"
  version = "1.0.0"
}

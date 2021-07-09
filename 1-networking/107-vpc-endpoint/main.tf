########################################################################################################################
provider "aws" {
  region = var.region
  profile = "aws-workout"
}

provider "aws" {
  alias = "another-region"
  region = "us-east-1"
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
data "terraform_remote_state" "bastion-105" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    key = "105-bastion"
    region = var.tf-s3-region
  }
}


######################################################################################
## Show how to reach AWS services (example: S3 bucket) using (or not) a VPC Endpoint
## 1) create a S3 bucket
## 2) create a VPC S3 Gateway Endpoint connected
## 3) show routes

## Add S3 buckets
resource "aws_s3_bucket" "s3-bucket-1-107" {
  bucket = "unique-name-s3-bucket-1-107"   ## Change Unique Name
  tags = {
    Purpose: var.dojo
    Name: "net-107-s3-bucket-1"
    Description: "First Bucket for DOJO 107"
  }
}
resource "aws_s3_bucket" "s3-bucket-2-107" {
  bucket = "unique-name-s3-bucket-2-107"   ## Change Unique Name
  provider = aws.another-region
  tags = {
    Purpose: var.dojo
    Name: "net-107-s3-bucket-2"
    Description: "Second Bucket for DOJO 107"
  }
}
resource "aws_vpc_endpoint" "vpc-endpoint-1-107" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  service_name = "com.amazonaws.${var.region}.s3"
  route_table_ids = [data.terraform_remote_state.bastion-105.outputs.net-105-rt-2-id]
}


########################################################################################################################
variable "vpc_id" {
  type = string
}

variable "public_subnet_102_id" {
  type = string
}

variable "private_subnet_102_id" {
  type = string
}

variable "private_route_table_105_id" {
  type = string
}

variable "private_security_group_105_id" {
  type = string
}

provider "aws" {
  alias = "another-region"
  region = var.another-region
  profile = "aws-workout"
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
  vpc_id = var.vpc_id
  service_name = "com.amazonaws.${var.region}.s3"
  route_table_ids = [var.private_route_table_105_id]
}


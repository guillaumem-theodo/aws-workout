########################################################################################################################
variable "vpc_id" {
  type = string
}

variable "private_route_table_105_id" {
  type = string
}

######################################################################################
## Show how to reach AWS services (example: S3 bucket) using (or not) a VPC Endpoint
## 1) create a VPC S3 Gateway Endpoint
## 2) show routes

resource "aws_vpc_endpoint" "net-107-vpc-endpoint-1" {
  vpc_id = var.vpc_id
  service_name = "com.amazonaws.${var.region}.s3"
  route_table_ids = [var.private_route_table_105_id]
  tags = {
    Purpose: var.dojo
    Name: "net-107-vpc-endpoint-1"
  }
}


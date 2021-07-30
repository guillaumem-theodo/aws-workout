########################################################################################################################
provider "aws" {
  region = var.region
  profile = "aws-workout"
}
provider "aws" {
  alias = "another-region"
  region = var.another-region
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

data "terraform_remote_state" "s3-buckets-301" {
  backend = "s3"
  config = {
    bucket = var.tf-s3-bucket
    key = "301-S3"
    region = var.tf-s3-region
  }
}

data "aws_iam_user" "second-user" {
  user_name = "aws-workout-second-user"
}

resource "aws_vpc_endpoint" "vpc-endpoint-1-302" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  service_name = "com.amazonaws.${var.region}.s3"
  route_table_ids = [aws_route_table.route-table-302-1.id]
}

########################################################################################################################
## Add policies to S3 buckets
## 1. Policy that DENIES GetObject to One Specific user (need a second profile)
## 2. Policy that DENIES GetObject if not initiated from a specific public IP  (e.g myIP or EC2 IP)
## 3. Policy that DENIES GetObject if not initiated through a given VPC Endpoint (gateway)

########################################################################################################################

resource "aws_s3_bucket_policy" "s3-policy-sto-302-1" {
  bucket = data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-1-id
  policy = jsonencode({
    Version = "2012-10-17"
    Id      = "myBucketPolicy2"
    Statement = [
      {
        Sid       = "AllowGetObjectFromOneUserOnly"
        Effect    = "Deny"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource = [
          data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-1-arn,
          "${data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-1-arn}/*",
        ]
        Condition = {
          "StringNotLike": {
            "aws:userId": [
              data.aws_iam_user.second-user.id
            ]
          }
        }
      },
      {
        Sid       = "AllFromEveryBody"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:*"
        Resource = [
          data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-1-arn,
          "${data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-1-arn}/*",
        ]
      },
    ]
  })
}

resource "aws_s3_bucket_policy" "s3-policy-sto-302-2" {
  bucket = data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-2-id
  provider = aws.another-region
  policy = jsonencode({
    Version = "2012-10-17"
    Id      = "myBucketPolicy1"
    Statement = [
      {
        Sid       = "AllowGetObjectFromSomeIPsOnly"
        Effect    = "Deny"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource = [
          data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-2-arn,
          "${data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-2-arn}/*",
        ]
        Condition = {
          NotIpAddress = {
            "aws:SourceIp" = [
              "${module.myip.address}/32",
              "${aws_instance.worker-ec2-1.public_ip}/32"]
          }
        }
      },
      {
        Sid       = "AllowAllOtherActionsFromEveryWhere"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:*"
        Resource = [
          data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-2-arn,
          "${data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-2-arn}/*",
        ]
      },
    ]
  })
}

resource "aws_s3_bucket_policy" "s3-policy-sto-302-3" {
  bucket = data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-3-id
  policy = jsonencode({
    Version = "2012-10-17"
    Id      = "myBucketPolicy3"
    Statement = [
      {
        Sid       = "AllowGetObjectFromVpcEndpointOnly"
        Effect    = "Deny"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource = [
          data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-3-arn,
          "${data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-3-arn}/*",
        ]
        Condition = {
            "StringNotEquals": {
              "aws:SourceVpce": aws_vpc_endpoint.vpc-endpoint-1-302.id
            }
        }
      },
      {
        Sid       = "AllFromEveryBody"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:*"
        Resource = [
          data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-3-arn,
          "${data.terraform_remote_state.s3-buckets-301.outputs.sto-301-bucket-3-arn}/*",
        ]
      },
    ]
  })
}


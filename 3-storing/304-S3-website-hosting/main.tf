########################################################################################################################
provider "aws" {
  region = var.region
  profile = "aws-workout"
}

########################################################################################################################
## S3 bucket supports Versioning

resource "aws_s3_bucket" "s3-bucket-1-304" {
  bucket = "unique-name-s3-bucket-1-304"   ## Change Unique Name
  force_destroy = true
  website {
    index_document = "index.html"
    error_document = "index.html"
  }
  acl    = "public-read"
  tags = {
    Purpose: var.dojo
    Name: "sto-304-s3-bucket-1"
    Description: "Bucket for Web site DOJO 304 in region ${var.region}"
  }
}

resource "aws_s3_bucket_object" "s3-bucket-object-1-304" {
  bucket = aws_s3_bucket.s3-bucket-1-304.bucket
  key    = "index.html"
  source = "index.html"
  acl    = "public-read"
  etag = filemd5("index.html")
}

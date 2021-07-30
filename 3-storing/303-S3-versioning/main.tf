########################################################################################################################
provider "aws" {
  region = var.region
  profile = "aws-workout"
}

########################################################################################################################
## S3 bucket supports Versioning

resource "aws_s3_bucket" "s3-bucket-1-303" {
  bucket = "unique-name-s3-bucket-1-303"   ## Change Unique Name
  force_destroy = true

  versioning {
    enabled = true
  }
  tags = {
    Purpose: var.dojo
    Name: "sto-303-s3-bucket-1"
    Description: "First Bucket for DOJO 303 in region ${var.region}"
  }
}

resource "aws_s3_bucket" "s3-bucket-2-303" {
  bucket = "unique-name-s3-bucket-2-303"   ## Change Unique Name
  force_destroy = true
  versioning {
    enabled = false
  }
  tags = {
    Purpose: var.dojo
    Name: "sto-303-s3-bucket-2"
    Description: "Second Bucket for DOJO 303 in region ${var.region}"
  }
}

resource "aws_s3_bucket_object" "s3-bucket-object-1-303" {
  bucket = aws_s3_bucket.s3-bucket-1-303.bucket
  key    = "my-key-1"
  source = "fixtures/file1.txt"
  etag = filemd5("fixtures/file1.txt")
  tags = {
    functional_version: "my version 1"
  }
}

resource "aws_s3_bucket_object" "s3-bucket-object-2-303" {
  bucket = aws_s3_bucket.s3-bucket-1-303.bucket
  key    = "my-key-1"
  source = "fixtures/file1_v2.txt"
  etag = filemd5("fixtures/file1_v2.txt")
  tags = {
    functional_version: "my version 2"
  }
}

resource "aws_s3_bucket_object" "s3-bucket-object-3-303" {
  bucket = aws_s3_bucket.s3-bucket-1-303.bucket
  key    = "my-key-1"
  source = "fixtures/file1_v3.txt"
  etag = filemd5("fixtures/file1_v3.txt")
  tags = {
    functional_version: "my version 3"
  }
}

resource "aws_s3_bucket_object" "s3-bucket-object-4-303" {
  bucket = aws_s3_bucket.s3-bucket-2-303.bucket
  key    = "my-key-2"
  source = "fixtures/file2.txt"
  etag = filemd5("fixtures/file2.txt")
  tags = {
    functional_version: "my version 1"
  }
}

resource "aws_s3_bucket_object" "s3-bucket-object-5-303" {
  bucket = aws_s3_bucket.s3-bucket-2-303.bucket
  key    = "my-key-2"
  source = "fixtures/file2_v2.txt"
  etag = filemd5("fixtures/file2_v2.txt")
  tags = {
    functional_version: "my version 2"
  }
}

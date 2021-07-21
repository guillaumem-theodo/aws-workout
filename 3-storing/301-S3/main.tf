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

########################################################################################################################
## S3 is an object store
## S3 objects are stored in BUCKETS
## Buckets names MUST be unique Worldwide across all AWS users
## Objects are stored in buckets with a KEY
## A key can be "path like" (mySubGroup/mySecondSubGroup/myObject)
## Objects can be retrieved using Console, CLI, SDK using the pair (bucket, Key)
## S3 is a global service (when using the AWS Console, you see buckets from all the regions)

resource "aws_s3_bucket" "s3-bucket-1-301" {
  bucket = "unique-name-s3-bucket-1-301"   ## Change Unique Name
  force_destroy = true
  tags = {
    Purpose: var.dojo
    Name: "sto-301-s3-bucket-1"
    Description: "First Bucket for DOJO 301 in region ${var.region}"
  }
}

resource "aws_s3_bucket" "s3-bucket-2-301" {
  bucket = "unique-name-s3-bucket-2-301"   ## Change Unique Name
  force_destroy = true
  provider = aws.another-region
  tags = {
    Purpose: var.dojo
    Name: "sto-301-s3-bucket-2"
    Description: "Second Bucket for DOJO 301 in region ${var.another-region}"
  }
}

resource "aws_s3_bucket" "s3-bucket-3-301" {
  bucket = "unique-name-s3-bucket-3-301"   ## Change Unique Name
  force_destroy = true
  tags = {
    Purpose: var.dojo
    Name: "sto-301-s3-bucket-3"
    Description: "Third Bucket for DOJO 301 in region ${var.region}"
  }
}

resource "aws_s3_bucket" "s3-bucket-4-301" {
  bucket = "unique-name-s3-bucket-4-301"   ## Change Unique Name
  force_destroy = true
  tags = {
    Purpose: var.dojo
    Name: "sto-301-s3-bucket-4"
    Description: "Fourth Bucket for DOJO 301 in region ${var.region}"
  }
}

resource "aws_s3_bucket_object" "s3-bucket-object-1-301" {
  bucket = aws_s3_bucket.s3-bucket-1-301.bucket
  key    = "my-key-1"
  source = "fixtures/file1.txt"
  etag = filemd5("fixtures/file1.txt")
}

resource "aws_s3_bucket_object" "s3-bucket-object-2-301" {
  bucket = aws_s3_bucket.s3-bucket-1-301.bucket
  key    = "mySubGroup/mySecondSubGroup/my-key-2"
  source = "fixtures/file2.txt"
  etag = filemd5("fixtures/file2.txt")
}

resource "aws_s3_bucket_object" "s3-bucket-object-3-301" {
  bucket = aws_s3_bucket.s3-bucket-1-301.bucket
  key    = "mySubGroup/mySecondSubGroup/my-key-3"
  source = "fixtures/file3.txt"
  etag = filemd5("fixtures/file3.txt")
}

resource "aws_s3_bucket_object" "s3-bucket-object-4-301" {
  bucket = aws_s3_bucket.s3-bucket-2-301.bucket
  provider = aws.another-region
  key    = "my-key-3"
  source = "fixtures/file3.txt"
  etag = filemd5("fixtures/file3.txt")
}

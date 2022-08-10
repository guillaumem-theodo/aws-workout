## Add S3 buckets
resource "aws_s3_bucket" "common-s3-bucket-1" {
  bucket = "${var.unique-key}-s3-bucket-1"
  force_destroy = true
  tags = {
    Purpose: var.dojo
    Name: "common-s3-bucket-1"
    Description: "First Bucket for DOJO in first region ${var.region}"
  }
}
resource "aws_s3_bucket" "common-s3-bucket-2" {
  bucket = "${var.unique-key}-s3-bucket-2"
  provider = aws.another-region ## Use the second AWS provider (in the second region)
  force_destroy = true
  tags = {
    Purpose: var.dojo
    Name: "common-s3-bucket-2"
    Description: "Second Bucket for DOJO in second region ${var.another-region}"
  }
}

resource "aws_s3_object" "s3-bucket-object-1" {
  bucket = aws_s3_bucket.common-s3-bucket-1.bucket
  key    = "my-key-1"
  source = "fixtures/file1.txt"
  etag = filemd5("fixtures/file1.txt")

}

resource "aws_s3_object" "s3-bucket-object-2" {
  bucket = aws_s3_bucket.common-s3-bucket-1.bucket
  key    = "mySubGroup/mySecondSubGroup/my-key-2"
  source = "fixtures/file2.txt"
  etag = filemd5("fixtures/file2.txt")
}

resource "aws_s3_object" "s3-bucket-object-3" {
  bucket = aws_s3_bucket.common-s3-bucket-1.bucket
  key    = "mySubGroup/mySecondSubGroup/my-key-3"
  source = "fixtures/file3.txt"
  etag = filemd5("fixtures/file3.txt")
}

resource "aws_s3_object" "s3-bucket-object-4" {
  bucket = aws_s3_bucket.common-s3-bucket-2.bucket
  provider = aws.another-region
  key    = "my-key-3"
  source = "fixtures/file3.txt"
  etag = filemd5("fixtures/file3.txt")
}

resource "aws_s3_object" "s3-bucket-object-5" {
  bucket = aws_s3_bucket.common-s3-bucket-2.bucket
  provider = aws.another-region
  key    = "my-key-3"
  source = "fixtures/file3.txt"
  etag = filemd5("fixtures/file3.txt")
}

output "common_s3_bucket_1_name" {
  value = aws_s3_bucket.common-s3-bucket-1.bucket
}
output "common_s3_bucket_2_name" {
  value = aws_s3_bucket.common-s3-bucket-2.bucket
}

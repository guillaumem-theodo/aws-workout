resource "aws_s3_bucket" "cpu-209-s3-bucket" {
  bucket = "${var.unique-key}-cpu-209-s3-bucket"   ## Change Unique Name
  force_destroy = true
  tags   = {
    Purpose : var.dojo
    Name : "${var.unique-key}-cpu-209-s3-bucket"
    Description : "Bucket that will be accessible only from VPC through VPC Endpoint"
  }
}

#############################################################################
## CREATE A VPC ENDPOINT TO S3 in the region
resource "aws_vpc_endpoint" "cpu-209-vpc-endpoint-1" {
  vpc_id          = var.vpc_id
  service_name    = "com.amazonaws.${var.region}.s3"
  route_table_ids = [aws_route_table.cpu-209-rt-1.id, aws_route_table.cpu-209-rt-2.id]
}

#############################################################################
## AUTHORIZE ListBucket ONLY from VPC Endpoint
resource "aws_s3_bucket_policy" "cpu-209-s3-bucket-policy" {
  bucket = aws_s3_bucket.cpu-209-s3-bucket.id
  policy = jsonencode({
    Version   = "2012-10-17"
    Id        = "AllowAccessFromVPCEndpoint"
    Statement = [
      {
        Sid       = "VPCEndpointAccess"
        Effect    = "Deny"
        Principal = "*"
        Action    = ["s3:GetObject"]
        Resource  = [
          aws_s3_bucket.cpu-209-s3-bucket.arn,
          "${aws_s3_bucket.cpu-209-s3-bucket.arn}/*",
        ]

        Condition = {
          StringNotEquals = { "aws:SourceVpce" = aws_vpc_endpoint.cpu-209-vpc-endpoint-1.id, }
        }
      },
      {
        Sid       = "AllOtherOk"
        Effect    = "Allow"
        Principal = "*"
        Action    = ["s3:*"]
        Resource  = [
          aws_s3_bucket.cpu-209-s3-bucket.arn,
          "${aws_s3_bucket.cpu-209-s3-bucket.arn}/*",
        ]
      }
    ]
  })
}

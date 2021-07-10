resource "aws_s3_bucket" "s3-bucket-1-208" {
  bucket = "unique-name-s3-bucket-1-208"   ## Change Unique Name
  tags = {
    Purpose: var.dojo
    Name: "net-208-s3-bucket-1"
    Description: "Bucket that will be accessible only from VPC through VPC Endpoint"
  }
}
resource "aws_s3_bucket_object" "s3-bucket-object-1-208" {
  bucket = aws_s3_bucket.s3-bucket-1-208.bucket
  key    = "a_file_uploaded_in_bucket"
  source = "README.md"
}

/*
resource "aws_vpc_endpoint" "vpc-endpoint-1-208" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  service_name = "com.amazonaws.${var.region}.s3"
  route_table_ids = [aws_route_table.route-table-208-1.id]
}


resource "aws_s3_bucket_policy" "s3-bucket-policy-208" {
  bucket = aws_s3_bucket.s3-bucket-1-208.id
  # Terraform expression's result to valid JSON syntax.
  policy = jsonencode({
    Version = "2012-10-17"
    Id      = "AllowAccessFromVPCEndpointOnly"
    Statement = [
      {
        Sid       = "VPCEndpointAccess"
        Effect    = "Deny"
        Principal = "*"
        Action    = ["s3:ListBucket", "s3:*Object*"]
        Resource = [
          aws_s3_bucket.s3-bucket-1-208.arn,
          "${aws_s3_bucket.s3-bucket-1-208.arn}/*",
        ]
        Condition = {
          StringNotEquals = {
            "aws:SourceVpce" = aws_vpc_endpoint.vpc-endpoint-1-208.id
          }
        }
      },
    ]
  })
}
*/

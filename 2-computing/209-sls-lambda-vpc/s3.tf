resource "aws_s3_bucket" "s3-bucket-1-209" {
  bucket = "unique-name-s3-bucket-1-209"   ## Change Unique Name
  tags = {
    Purpose: var.dojo
    Name: "net-209-s3-bucket-1"
    Description: "Bucket that will be accessible only from VPC through VPC Endpoint"
  }
}
resource "aws_s3_bucket_object" "s3-bucket-object-1-209" {
  bucket = aws_s3_bucket.s3-bucket-1-209.bucket
  key    = "a_file_uploaded_in_bucket"
  source = "README.md"
}

#############################################################################
## CREATE A VPC ENDPOINT TO S3 in the region
resource "aws_vpc_endpoint" "vpc-endpoint-1-209" {
  vpc_id = data.terraform_remote_state.vpc-101.outputs.net-101-vpc-id
  service_name = "com.amazonaws.${var.region}.s3"
  route_table_ids = [aws_route_table.route-table-209-1.id, aws_route_table.route-table-209-2.id]
}

#############################################################################
## AUTHORIZE ListBucket ONLY from VPC Endpoint
resource "aws_s3_bucket_policy" "s3-bucket-policy-209" {
  bucket = aws_s3_bucket.s3-bucket-1-209.id
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
          aws_s3_bucket.s3-bucket-1-209.arn,
          "${aws_s3_bucket.s3-bucket-1-209.arn}/*",
        ]

        ## THIS CONDITION DENY the actions, if the source of the request is NOT the VPCE
        Condition = {
          StringNotEquals = {
            "aws:SourceVpce" = aws_vpc_endpoint.vpc-endpoint-1-209.id
          }
        }
      },
    ]
  })
}


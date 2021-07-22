resource "aws_iam_role" "iam-role-302" {
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF

  tags = {
    Purpose: var.dojo
    Name: "sto-302-iam-role-1"
    Description: "A role for the EC2 that allows the EC2 to assume some roles on your behalf"
  }
}

resource "aws_iam_policy_attachment" "s3-policy-attached-to-role" {
  name = "sto-302-iam-role-1-policy-attachment"
  roles      = [aws_iam_role.iam-role-302.id]
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

resource "aws_iam_instance_profile" "instance-profile" {
  role = aws_iam_role.iam-role-302.id
  name = "sto-302-instance-profile-1"
  tags = {
    Purpose: var.dojo
    Name: "sto-302-instance-profile-1"
  }
}

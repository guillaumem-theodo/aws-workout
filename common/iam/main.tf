resource "aws_iam_role" "common-iam-ec2-role" {
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
    Name: "common-role-for-ec2"
    Description: "A role for the EC2 that allows the EC2 to assume some roles on your behalf"
  }
}

resource "aws_iam_policy_attachment" "common-s3-policy-attached-to-role" {
  name = "common-iam-role-attachment"
  roles      = [aws_iam_role.common-iam-ec2-role.id]
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

resource "aws_iam_instance_profile" "common-instance-profile" {
  role = aws_iam_role.common-iam-ec2-role.id
  name = "common-ec2-instance-profile"
  tags = {
    Purpose: var.dojo
    Name: "common-ec2-instance-profile"
  }
}

output "iam_ec2_instance_profile_id" {
  value = aws_iam_instance_profile.common-instance-profile.id
}

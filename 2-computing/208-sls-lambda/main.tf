########################################################################################################################
variable "vpc_id" {
  type = string
}

variable "subnet1_102_id" {
  type = string
}
variable "subnet2_102_id" {
  type = string
}
variable "s3_bucket1" {
  type = string
}
variable "s3_bucket2" {
  type = string
}

data "aws_caller_identity" "current" {}

########################################################################################################################
## ALLOW 'TEST' EC2 to use Read-only S3 actions
########################################################################################################################

resource "aws_iam_role" "cpu-208-iam-role-1" {
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
    Name: "cpu-208-iam-role-1"
  }
}

resource "aws_iam_policy_attachment" "cpu-208-policy-attached-to-role" {
  name = "cpu-208-policy-attached-to-role"
  roles      = [aws_iam_role.cpu-208-iam-role-1.id]
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

resource "aws_iam_instance_profile" "cpu-208-instance-profile-1" {
  role = aws_iam_role.cpu-208-iam-role-1.id
  name = "cpu-208-instance-profile-1"
  tags = {
    Purpose: var.dojo
    Name: "cpu-208-instance-profile-1"
  }
}

########################################################################################################################
## TEST EC2

resource "aws_instance" "cpu-208-ec2-test-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = var.subnet1_102_id
  vpc_security_group_ids = [aws_security_group.cpu-208-sg-1.id]
  key_name = "aws-workout-key"
  iam_instance_profile = aws_iam_instance_profile.cpu-208-instance-profile-1.id

  tags = {
    Purpose: var.dojo
    Name: "cpu-208-ec2-test-1"
  }
}

########################################################################################################################
## Build code zip (do not deploy)
resource "null_resource" "build-sls" {
  provisioner "local-exec" {
    command = "(cd sls; yarn; yarn build)"
  }
}

########################################################################################################################
resource "aws_iam_role" "cpu-208-role-for-lambda" {
  name = "cpu-208-role-for-lambda"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF

  managed_policy_arns = [
    "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess",
    "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"]
}

########################################################################################################################
resource "aws_lambda_function" "cpu-208-lambda-1" {
  depends_on = [null_resource.build-sls]

  function_name = "cpu-208-lambda-1"

  filename      = "./sls/.serverless/cpu-208-sls-lambda.zip"
  role          = aws_iam_role.cpu-208-role-for-lambda.arn
  handler       = "handler.listAllObjects"

  runtime = "nodejs12.x"

  environment {
    variables = {
      BUCKET_NAME= var.s3_bucket1
    }
  }

}

########################################################################################################################
resource "aws_api_gateway_rest_api" "cpu-208-api-gtw" {
  name = "dev-cpu-208-api-gtw"
}

resource "aws_api_gateway_resource" "cpu-208-api-gtw-resource" {
  path_part   = "demo-208"
  parent_id   = aws_api_gateway_rest_api.cpu-208-api-gtw.root_resource_id
  rest_api_id = aws_api_gateway_rest_api.cpu-208-api-gtw.id
}

resource "aws_api_gateway_method" "cpu-208-api-gtw-method" {
  rest_api_id   = aws_api_gateway_rest_api.cpu-208-api-gtw.id
  resource_id   = aws_api_gateway_resource.cpu-208-api-gtw-resource.id
  http_method   = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "cpu-208-api-gtw-integration" {
  rest_api_id             = aws_api_gateway_rest_api.cpu-208-api-gtw.id
  resource_id             = aws_api_gateway_resource.cpu-208-api-gtw-resource.id
  http_method             = aws_api_gateway_method.cpu-208-api-gtw-method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.cpu-208-lambda-1.invoke_arn
}

resource "aws_lambda_permission" "cpu-208-lambda-permission-for-api-gtw" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.cpu-208-lambda-1.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${var.region}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.cpu-208-api-gtw.id}/*/${aws_api_gateway_method.cpu-208-api-gtw-method.http_method}${aws_api_gateway_resource.cpu-208-api-gtw-resource.path}"
}

########################################################################################################################
resource "aws_api_gateway_deployment" "cpu-208-api-gtw-deployment" {
  depends_on = [
    aws_api_gateway_method.cpu-208-api-gtw-method,
    aws_api_gateway_integration.cpu-208-api-gtw-integration
  ]

  rest_api_id = aws_api_gateway_rest_api.cpu-208-api-gtw.id

  triggers = {
    redeployment = sha1(jsonencode(aws_api_gateway_rest_api.cpu-208-api-gtw.body))
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_stage" "cpu-208-api-gtw-stage" {
  deployment_id = aws_api_gateway_deployment.cpu-208-api-gtw-deployment.id
  rest_api_id   = aws_api_gateway_rest_api.cpu-208-api-gtw.id
  stage_name    = "dev"
}

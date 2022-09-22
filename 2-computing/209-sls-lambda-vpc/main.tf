variable "vpc_id" {
  type = string
}

variable "subnet1_102_id" {
  type = string
}
variable "subnet3_102_id" {
  type = string
}

data "aws_caller_identity" "current" {}

########################################################################################################################
## Build code zip (do not deploy)
resource "null_resource" "build-sls" {
  provisioner "local-exec" {
    command = "(cd sls; yarn; yarn build)"
  }
}

########################################################################################################################
resource "aws_iam_role" "cpu-209-role-for-lambda" {
  name = "cpu-209-role-for-lambda"

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
    "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
  ]

  inline_policy {
    name = "lambda_in_vpc_needs_ec2_eni"

    policy = jsonencode({
      Version   = "2012-10-17"
      Statement = [
        {
          Action = [
            "ec2:DescribeNetworkInterfaces",
            "ec2:CreateNetworkInterface",
            "ec2:DeleteNetworkInterface",
            "ec2:DescribeInstances",
            "ec2:AttachNetworkInterface"
          ]
          Effect   = "Allow"
          Resource = "*"
        },
      ]
    })
  }
}

########################################################################################################################
resource "aws_lambda_function" "cpu-209-lambda-1" {
  depends_on = [null_resource.build-sls]

  function_name = "cpu-209-lambda-1"

  filename = "./sls/.serverless/service-209-sls-lambda-vpc.zip"
  role     = aws_iam_role.cpu-209-role-for-lambda.arn
  handler  = "handler.listAllObjects"

  vpc_config {
    security_group_ids = [aws_security_group.cpu-209-sg-2-private.id]
    subnet_ids         = [var.subnet3_102_id]
  }

  runtime = "nodejs12.x"

  environment {
    variables = {
      BUCKET_NAME = aws_s3_bucket.cpu-209-s3-bucket.bucket
    }
  }

}

########################################################################################################################
resource "aws_api_gateway_rest_api" "cpu-209-api-gtw" {
  name = "dev-cpu-209-api-gtw"
}

resource "aws_api_gateway_resource" "cpu-209-api-gtw-resource" {
  path_part   = "demo-209"
  parent_id   = aws_api_gateway_rest_api.cpu-209-api-gtw.root_resource_id
  rest_api_id = aws_api_gateway_rest_api.cpu-209-api-gtw.id
}

resource "aws_api_gateway_method" "cpu-209-api-gtw-method" {
  rest_api_id   = aws_api_gateway_rest_api.cpu-209-api-gtw.id
  resource_id   = aws_api_gateway_resource.cpu-209-api-gtw-resource.id
  http_method   = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "cpu-209-api-gtw-integration" {
  rest_api_id             = aws_api_gateway_rest_api.cpu-209-api-gtw.id
  resource_id             = aws_api_gateway_resource.cpu-209-api-gtw-resource.id
  http_method             = aws_api_gateway_method.cpu-209-api-gtw-method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.cpu-209-lambda-1.invoke_arn
}

resource "aws_lambda_permission" "cpu-209-lambda-permission-for-api-gtw" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.cpu-209-lambda-1.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "arn:aws:execute-api:${var.region}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.cpu-209-api-gtw.id}/*/${aws_api_gateway_method.cpu-209-api-gtw-method.http_method}${aws_api_gateway_resource.cpu-209-api-gtw-resource.path}"
}

########################################################################################################################
resource "aws_api_gateway_deployment" "cpu-209-api-gtw-deployment" {
  depends_on = [
    aws_api_gateway_method.cpu-209-api-gtw-method,
    aws_api_gateway_integration.cpu-209-api-gtw-integration
  ]

  rest_api_id = aws_api_gateway_rest_api.cpu-209-api-gtw.id

  triggers = {
    redeployment = sha1(jsonencode(aws_api_gateway_rest_api.cpu-209-api-gtw.body))
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_stage" "cpu-209-api-gtw-stage" {
  deployment_id = aws_api_gateway_deployment.cpu-209-api-gtw-deployment.id
  rest_api_id   = aws_api_gateway_rest_api.cpu-209-api-gtw.id
  stage_name    = "dev"
}


## IMPORTED TERRAGRUNT ROOT FILE

## Shared AWS_PROFILE for all Workouts
locals {
  aws_profile = "aws-workout"
}

## Shared REMOTE STATE IN S3 for all Workouts
remote_state {
  backend = "s3"
  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }
  config = {
    region = get_env("TERRAFORM_BACKEND_S3_BUCKET_REGION")
    bucket = get_env("TERRAFORM_BACKEND_S3_BUCKET")
    key = "${path_relative_to_include()}/terraform.tfstate"
    profile = "${local.aws_profile}"
    encrypt = true
  }
}

## Shared PROVIDER for all Workouts
generate "provider" {
  path = "provider.tf"
  if_exists = "overwrite_terragrunt"
  contents = <<EOF
provider "aws" {
  region = var.region
  profile = "aws-workout"
}
EOF
}

terraform {
  ## Read Commons variables and plugin (shared with all tutorials)
  source = "${get_parent_terragrunt_dir()}/common"

  ## Setup AWS_PROFILE for all commands for all tutorials
  extra_arguments "aws_profile" {
    commands = [
      "init",
      "apply",
      "refresh",
      "import",
      "plan",
      "destroy"
    ]

    env_vars = {
      AWS_PROFILE = "${local.aws_profile}"
    }
  }

  ## Inject common variables to all tutorials
  extra_arguments "custom_vars" {
    commands = [
      "init",
      "apply",
      "refresh",
      "import",
      "plan",
      "destroy"
    ]

    arguments = [
      "-var", "region=${get_env("TUTORIAL_REGION")}",
      "-var", "another-region=us-${get_env("TUTORIAL_ANOTHER_REGION")}"
    ]
  }
}

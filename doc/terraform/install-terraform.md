## Set up TERRAFORM CLI on your local

### Install TERRAFORM CLI

- You need to have TERRAFORM CLI installed on your computer : https://learn.hashicorp.com/tutorials/terraform/install-cli
- You can check your Terraform CLI version: `terraform --version` (Tutorials have been tested with version 1.0.1)

### Install TERRAGRUNT
- [Terragrunt](https://terragrunt.gruntwork.io) is a Terraform wrapper that provides extra tools used in these tutorials
- Tutorials share the Terraform backend state in S3. Terraform S3 backend is setup and shared using Terragrunt
- Tutorials share Terraform common variables and plugins setups using Terragrunt
- Tutorials are chained linked (some tutorials require other tutorials setup) using Terragrunt
- You need to install Terragrunt. [See](https://terragrunt.gruntwork.io/docs/getting-started/install/)
- You can check that Terragrunt CLI is properly installed: `terragrunt -v` (Tutorials have been tested with terragrunt v0.36.1)

### Create a S3 bucket for Terraform states
Terraform stores its states in a S3 bucket. 

- Choose an AWS S3 bucket name. It must be worldwide unique. E.g. gmi-2022-aws-workouts
- Put the name of the bucket and the region of the bucket in the `backend-env.conf` [file](../../backend-env.conf)

```
export TERRAFORM_BACKEND_S3_BUCKET_REGION=<PUT YOUR BUCKET REGION HERE>
export TERRAFORM_BACKEND_S3_BUCKET=<PUT YOUR BUCKET NAME HERE>
```


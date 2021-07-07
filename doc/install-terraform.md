## Set up TERRAFORM CLI on your local

### Install TERRAFORM CLI

- You need to have TERRAFORM CLI installed on your computer : https://learn.hashicorp.com/tutorials/terraform/install-cli
- You can check your Terraform CLI version: `terraform --version` (DOJOs have been tested with version 0.13.2)

### Create a S3 bucket for Terraform states
Terraform stores its states in a S3 bucket. 

- In AWS Console, create a S3 bucket with a unique name
- Put the name of the bucket and the region of the bucket in the `backend-env.conf` file

```
export S3_BUCKET_REGION=<PUT YOUR BUCKET REGION HERE>
export S3_BUCKET=<PUT YOUR BUCKET NAME HERE>
```


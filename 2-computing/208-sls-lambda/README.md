## Lambda using Serverless framework

⭐⭐⭐ (more complexe)️ ⭐⭐⭐

⚠️️ For this workout you will need to install **Serverless Framework**

It could be possible to do the workout serverless framework (using CloudFormation or Terraform).
Nevertheless, Serveless Framework eases the pain.

1️⃣ Install Serverless Framework globally on your laptop (you need npm)

```bash
npm install -g serverless
```  

2️⃣ Create a new Serverless Project in `208-sls-lambda/sls` directory

```bash
cd ./2-computing/208-sls-lambda
mkdir sls
cd sls
serverless create --template aws-nodejs-typescript
yarn
```

2️⃣ Modify the Terraform files

1) Create a S3 bucket, with an object inside
   

2) Create a public network (VPC, Subnet, Security Group)
    - allow SSH from your IP
    
    
3) Create an EC2 inside this network
    - allow the EC2 to perform S3:* actions on your behalf (see `204`)
    - you will be able to test S3 access from the EC2

3️⃣ Modify the SLS project
    - Modify the package.json to add dev dependency `aws-sdk`
    - Modify the lambda handler to list objects in the S3 bucket created (use `listObjectsV2` SDK method)
    - Pass the name of the bucket using env variable (if possible)

4️⃣ Trigger SLS deploy / SLS remove from Terraform

```hcl
resource "null_resource" "deploy-sls" {
  provisioner "local-exec" {
    command = "(cd sls; yarn; BUCKET_NAME=${aws_s3_bucket.s3-bucket-1-208.bucket} yarn deploy)"
  }

  provisioner "local-exec" {
    when    = destroy
    command = "(cd sls; serverless remove)"
  }
}
```


![Image of VPC](./doc/208-sls-lambda.png)





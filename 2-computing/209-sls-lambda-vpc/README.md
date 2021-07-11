## Lambda With a VPC access in order to access private S3 VPC Endpoint

⭐⭐⭐ (more complexe)️ ⭐⭐⭐

⚠️️ For this workout you will need to install **Serverless Framework**

As in `208 sls lambda` workout, we are going to deploy a Lambda to Enumerate an S3 bucket.

But in this workout, the S3 will be:
- accessed through a VPC Endpoint (Gateway Endpoint) (see `107` workout)
- protected to accept accesses ONLY from VPC Endpoint

As a consequence, the Lambda will have to be `attached` to the VPC (to be able to use the VPC Endpoint).

1️⃣ Same construct than in `208` workout

2️⃣ Add a S3 VPC Endpoint in the VPC (`107` workout)

3️⃣ Add a S3 Policy that blocks access on Read operations coming from other way than VPC Endpoint.

[Help Here](https://docs.aws.amazon.com/AmazonS3/latest/userguide/example-bucket-policies-vpc-endpoint.html)

4️⃣ Associate the Lambda to the VPC (security group and subnet)
In `serverless.ts` file.

🏁 Test S3 Enumeration
- ❌ Enumerate S3 objects from your laptop `aws s3 ls s3:\\bucketname`  (using your credential stored in profile). Should be KO since your lapop is not in the VPC
- ✅ Enumerate S3 objects from the EC2 `aws s3 ls s3:\\bucketname` (using the EC2 role)
- ✅ Enumerate S3 objects using the exposed API (using the Lambda Role)


![Image of VPC](./doc/209-sls-lambda-vpc.png)





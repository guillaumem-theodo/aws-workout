# Connecting S3 buckets through VPC endpoints

## Your mission
⚠️ From the `Bastion (105)` workouts

When you create an EC2, the traffic to/from other AWS Services uses Internet.
E.g: if you create an S3 bucket, and accesses this bucket from the EC2 using AWS API or AWS CLI, the data will be routed using internet.  
E.g: if you use a DynamoDb table from an EC2, query and data traffic will be routed through internet.  
E.g: if you use a KMS keys from an EC2, kms data will be routed through internet.  
This may be security, a performance, a reliability issues.  
**You may want to restrict traffic to use only AWS backbone network.**

AWS provides **VPC Endpoints** to make the traffic flows on private AWS backbone network.
This way, for example, when using the S3 API from your EC2, the traffic stays in private AWS network and does not requires internet access.

There are two types of **VPC Endpoints**:
- **Gateway VPC Endpoints** for S3 and DynamoDB services ONLY. 
  - Behind the scene, these gateway endpoints deal with Route Tables and PrefixLists (a list of IPs managed by AWS)
- **Interface VPC Endpoints** for ALL OTHERS services. 
  - Behind the scene, these interface endpoints deal with Elastic Network Interface (ENI).

(this difference is important for many certification questions 😉)

VPC endpoints are attached to a VPC and thus are **regional**.

1️⃣ Create two S3 buckets (NB: S3 bucket names MUST BE unique WORLDWIDE):
  - one located in the same region (e.g: eu-west-1) `net-107-s3-bucket-1`
  - one located in another region  (e.g: us-east-1) `net-107-s3-bucket-2`   

2️⃣ Add a VPC Endpoint for S3 (**Gateway** endpoint since the target service is S3)
  - Create a VPC Endpoint to S3 bucket in the first region
  - Modify the route table of the second subnet (``net-102-subnet-2``) to associate the VPC Endpoint

Remember that in `Bastion (105)` tutorial we saw that EC2 located inside private subnet ``net-102-subnet-2`` were not able to reach internet (and thus AWS services).
Nevertheless, having added a VPC endpoint for S3, EC2 is now able to reach S3 bucket (in the same region as the endpoint).

🏁 Test the routes to S3 buckets

- ✅ Observe the routes of the second subnet. 
  - Route table should contain an entry for S3 service `com.amazonws.eu-west-1.s3` in the same region. This route is automatically added when creating the gateway endpoint.
- ✅ On EC2 (ssh), use the `aws s3 ls --region eu-west-1 s3://thenameofyourfirstS3bucket` to see that S3 is accessible through VPC endpoint
- ❌ On EC2 (ssh), use the `aws s3 ls --region us-east-1 s3://thenameofyoursecondS3bucket` to see that S3 in second region is NOT accessible

## Support

[Doc AWS](https://docs.aws.amazon.com/vpc/latest/privatelink/vpc-endpoints.html)

![Image of VPC](./doc/107-vpc-endpoint.png)


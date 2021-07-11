## Connecting S3 buckets through VPC endpoints

⚠️ From the `Bastion (105)` and `NAT Gateway (106)`workouts

When you create an EC2, the traffic to/from other AWS Services uses Internet.
For example, if you create an S3 bucket, and accesses this bucket from one EC2, the data will be transfered using internet.

AWS provides **VPC Endpoints** to make the traffic flows on private AWS backbone. 
This way, when using the S3 (for example) from your EC2, the traffic stays in private AWS network.

There are two types of **VPC Endpoints**:
- **Gateway VPC Endpoints** for S3 and DynamoDB ONLY. These endpoints deal with Route Tables (behind the scene)
- **Interface VPC Endpoints** for ALL OTHERS services.

(this difference is important for many certification questions)

1️⃣ Create two S3 buckets (NB: S3 bucket names MUST BE unique WORLDWIDE):
  - one located in the same region (eu-west-1) `net-107-s3-bucket-1`
  - one located in another region `net-107-s3-bucket-2`   

2️⃣ Add a VPC Endpoint for S3 (**Gateway** endpoint)
  - Create a VPC Endpoint to S3 bucket in the first region
  - Modify the route table of the second subnet to associate the VPC Endpoint

🏁 Test the routes to S3 buckets

- ✅ Observe the routes of the second subnet. 
  - Route table should contain an entry for S3 service `com.amazonws.eu-west-1.s3` in the same region
- ✅ Using `traceroute` command, verify that outgoing traffic from second EC2 to the S3 in the same Region goes through the 👉 `VPC endpoint` (not internet)
- ✅ Using `traceroute` command, verify that outgoing traffic from second EC2 to the S3 in the **other** region goes through the 👉 `NAT gateway (internet)`. The first HOP in the route should be the NAT private IP

[Doc AWS](https://docs.aws.amazon.com/vpc/latest/privatelink/vpc-endpoints.html)

![Image of VPC](./doc/107-vpc-endpoint.png)

![VPC Endpoint Traceroutes](./doc/vpc-endpoint-s3.png)

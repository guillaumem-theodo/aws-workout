## Allow EC2 to perform actions on your behalf

As seen in the `203 metadata` workout, the EC2 launched is not able to connect to the S3 bucket.

By default, the EC2 launched have no right to execute any actions (any calls to AWS APIs) on your behalf. 
So, for example, the EC2 is not allowed to list the bucket content (even if the bucket allows the EC2 to do so).

In order to give action authorisation to the EC2, we need to create **an IAM Role** that will be associated to the EC2.
The Role will be, itself, associated with **policies**. Policies describe what is possible to do.

There are three types of policies:
- AWS Managed policies (predefined classical policies)
- Customer managed policies (reusable from roles to roles)  
- Inline policies (defined directly in the role)

## Your mission
1️⃣ Create an 👉 **IAM Role** named `cpu-204-iam-role-1`

2️⃣ Attach an AWS managed 👉 **IAM Policy** allowing **S3 read only commands** to the previously created role. 
The policy already exists in the catalog of predefined **AWS Managed policies**. The name of the policy is `AmazonS3ReadOnlyAccess`

3️⃣ Attach the **Role** to a new EC2. In order to do so, you need to create an **Instance Profile** linked to the IAM Role.
And then attached this Instance Profile to the EC2. EC2 can ONLY have ONE Instance Profile.

<div align="center">
<img src="./doc/204-ec2-role.png" width="800" alt="EC2Role">
</div>
<br>

## Your success
🏁 Test that the S3 is now reachable from the EC2
- ✅ You can see that now the EC2 can access S3 bucket content.

```bash
aws S3 ls s3://your-bucket-name
```

<div align="center">
<img src="./doc/s3-access-ok.png" width="800" alt="S3 Access OK">
</div>
<br>

You can use following commands to check your mission success
```shell
./launch.sh 2-computing/204-ec2-role/TEST-ssh-public-ec2.sh
./launch.sh 2-computing/204-ec2-role/TEST-s3-access.sh
```

## Materials
[Doc AWS](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/iam-roles-for-amazon-ec2.html)

### About STS Security

💀 Using the METADATA service you can now retrieve the temporary credentials (STS Token). 
This may be dangerous. A user that can execute a CURL on the EC2 can retrieve STS token. STS Token allows the user to call AWS API (APIs that are allowed by EC2 attached role). 
If many users can access your EC2, you should restrict which user is allowed to access **metadata**.
You should, in this case, consider **iptables** (see [here](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instancedata-data-retrieval.html))

```bash
curl 169.254.169.254/latest/meta-data/iam/security-credentials/xxxx
```

<div align="center">
<img src="./doc/meta-data-token-sts.png" width="800" alt="STS">
</div>
<br>






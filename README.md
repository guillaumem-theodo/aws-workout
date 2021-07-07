# 👨‍🎓 AWS Workout 👨‍🎓
## Small and Quick DOJO on AWS basic principles ##

### 🚀 Set up your AWS profile 🚀 
First follow [this documentation](./doc/install-aws.md) to set up a `aws-workout` AWS profile on your computer.

### 🏛 Tutorials organisation 🏛 

The DOJO steps are grouped by knowledge:
- Networking: [1-networking](./1-networking)   (VPC, subnets, security groups, peering, dns)
- Computing: 🚧 Under construction  (EC2, ECS, Lambdas)
- Storing: 🚧 Under construction (S3, RDS, Dynamodb)
- Protecting: 🚧 Under construction (KMS, S3, ACM)
- Publishing: 🚧 Under construction (CloudFront, pre-signed URL)
- Monitoring: 🚧 Under construction (CloudWatch, CloudTrail, FlowLogs)

In each knowledge area, the DOJO steps are ordered by difficulty. Example:
- 101-basic-vpc
- 102-basic-subnets
- 103-vpc-default-route-default-security-group
- 104-internet-access
- 105-nat-gateway
and so on...

### 🚀 How to launch DOJOs ? 🚀 

You can execute DOJOs with two modes:
- Terraform: you will learn **Terraform** while learning AWS [intro terraform](https://www.terraform.io/intro/index.html)
- AWS CloudFormation: you will learn **CloudFormation** while learning AWS [intro cloudformation](https://aws.amazon.com/fr/cloudformation/getting-started/)

It's your choice...
But **YOU CAN NOT SWITCH FROM TERRAFORM TO CLOUDFORMATION (or reverse).** Terraform DOJOs may require states from the previous ones. And Cloudformation DOJOs may require Stack outputs from previous ones.

### 🔑🔑 Create a Key Pair for your EC2 🔑🔑
In order to work with and to log into the EC2, you need to create a keypair
🚧 Howto:
- in the root directory `./generate-keypair.sh`
- it will create a keypair in AWS named `aws-workout-key`
- it will create the private and public key. 

### Terraform DOJOs 
If you want to use TERRAFORM versions, please install [Terraform CLI](./doc/install-terraform.md). 

#### 🚧 To apply a Terraform DOJO:
- in the root directory `./init-tutorial.sh xxxx` where **xxxx** is the DOJO directory. 
  - E.g: `./init-tutorial.sh ./1-networking/101-basic-vpc`
- this first step initializes the Terraform state in the S3 bucket for the selected DOJO
- then, run `./run-tutorial.sh xxxx` where **xxxx** is the DOJO directory.
- then, you can test the various `./launch.sh ./xxxx/TEST-yyyy.sh` commands to see the DOJO results

#### 🧹To delete a Terraform DOJO (and free AWS resources 💸💸💸):
- in the root directory `./delete-tutorial.sh xxxx` where **xxxx** is the DOJO directory. 
  - E.g: `./delete-tutorial.sh ./1-networking/101-basic-vpc`

### CloudFormation DOJOs 

#### 🚧 To apply a CloudFormation DOJO:
- in the root directory `./run-cf-tutorial.sh xxxx` where **xxxx** is the DOJO directory. 
  - E.g: `./run-cf-tutorial.sh ./1-networking/101-basic-vpc`
- go in the AWS Console and check the progress of your CloudFormation Stack    
- then, you can test the various `./launch.sh ./xxxx/TEST-yyyy.sh` commands to see the DOJO results

#### 🧹To delete a CloudFormation DOJO (and free AWS resources 💸💸💸):
- in the root directory `./delete-cf-tutorial.sh xxxx` where **xxxx** is the DOJO directory. 
  - E.g: `./delete-cf-tutorial.sh ./1-networking/101-basic-vpc`
- go in the AWS Console and check the deletion of your CloudFormation Stack    

# 👨‍🎓 AWS Workout 👨‍🎓
## Small and Quick Hands-on on AWS basic principles ##

This repository contains a set of workouts to train yourself on AWS services, using CDK or Terraform IAC frameworks.

### 🏛 Tutorials organisation 🏛 

I have grouped Workouts by knowledge categories:
- Networking: [1-networking](./1-networking)   (VPC, subnets, security groups, peering, dns, NAT gateway, VPC endpoints...)
- Computing: [2-computing](./2-computing)  (EC2, ECS, Lambdas, ALB, Auto-scaling)
- Storing: [3-storing](./3-storing)  (S3, RDS, Dynamodb)
- Protecting: 🕰 Soon...stay tuned (KMS, S3, ACM)
- Publishing: 🕰 Soon...stay tuned (CloudFront, pre-signed URL, OAI)
- Orchestrating: 🕰 Soon...stay tuned (Step Functions)

In each knowledge area, workouts are ordered by difficulty. Example:
- 101-basic-vpc
- 102-basic-subnets
- 103-vpc-default-route-default-security-group
- 104-internet-access
- 105-nat-gateway
and so on...

Some workouts rely on previous workouts (dependencies).
Provided shell commands to apply/delete workouts automatically verify required dependencies. 

### Pre-requisites

- Install ``jq`` : https://stedolan.github.io/jq/tutorial/
- Install ``AWS CLI`` : https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html 
- Install ``Terraform`` : https://learn.hashicorp.com/tutorials/terraform/install-cli
- Install ``Terragrunt``:  https://terragrunt.gruntwork.io/docs/getting-started/install
- Install ``Serverless Framework`` : https://www.serverless.com/framework/docs/getting-started
- Install ``CDK`` : https://docs.aws.amazon.com/cdk/v2/guide/getting_started.html

All workouts have been tested with:
- AWS CLI **2.2.10**
- Terraform **1.0.2**
- Terragrunt **0.36.1**
- AWS CDK **2.27.0**
- Serverless Framework **2.3.0**
- Mac OS 10.15
- Ubuntu 20.04 LTS

## 🔥🔥🔥DISCLAIMER🔥🔥🔥
Many of the resources created by these workouts induce costs. All these workouts execute on **YOUR AWS account**. 

You are responsible to delete workouts and associated resources, services or components at the end of workouts.
Otherwise, AWS will bill you...

I highly suggest you creating a budget and budget alerts in your AWS account.  
Follow this AWS [tutorial](https://docs.aws.amazon.com/cost-management/latest/userguide/budgets-create.html)

NB: Resources and components created using the provided stacks are tagged with a `Purpose` Tag.
You will be able to list all resources, using the **[AWS Tag Manager](https://docs.aws.amazon.com/ARG/latest/userguide/tag-editor.html)** in AWS Console.  
A ``./list-resources.sh`` shell command is also available to list all resources tagged.

## LET'S START
### 🚀 Set up your AWS profile 🚀 
First follow [this documentation](./doc/install-aws.md) to set up an AWS profile named `aws-workout` on your computer.
All shell commands (Terraform, CDK and tests) provided in these tutorials require this profile.

### 🚀 AWS EC2 key pairs 🚀 
Some workouts will create EC2 VMs that requires SSH key pairs to log-in.
Follow this tutorial to create and import a keypair in AWS [Key Pair](./doc/keypair.md).

### 🚀 How to perform Workouts ? 🚀
You can perform Workouts with two IAC frameworks:
- **Terraform**: you will see basics of **Terraform** and **Terragrunt** while learning AWS. [See Terraform](https://www.terraform.io/intro/index.html)
- **AWS CDK**: you will see basics of **CDK** while learning AWS. [See CDK](https://docs.aws.amazon.com/cdk/v2/guide/home.html)
  
These workouts do not intend to show **Terraform** or **CDK** best practices. 

⚠️ YOU CANNOT SWITCH FROM TERRAFORM TO CDK (and vice versa) ⚠️
There are differences between these two frameworks. For example:
- Terraform allows modifying default objects (routes...) whereas CDK does not.
- Terraform provides some syntactical sugar whereas CDK offers L1 constructs (low level), L2 (high order constructs)...
- Terraform workouts may require states from the previous workouts (stored in Terraform State S3 bucket). 
- CDK workouts may require stack outputs from previous workouts (stored in AWS CloudFormation Stacks). ️

## Terraform Workouts 
If you want to use TERRAFORM versions, go there 👉[Terraform Workouts](doc/terraform/README.md). 

## CDK Workouts 
If you want to use CDK versions, go there 👉[CDK Workouts](doc/cdk/README.md). 

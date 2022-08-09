# 👨‍🎓 AWS Workout 👨‍🎓
## Small and Quick Hands-on on AWS basic principles ##

### 🏛 Tutorials organisation 🏛 

We have grouped Workout Steps by knowledge categories:
- Networking: [1-networking](./1-networking)   (VPC, subnets, security groups, peering, dns)
- Computing: [2-computing](./2-computing)  (EC2, ECS, Lambdas, ALB, Auto-scaling)
- Storing: [3-storing](./3-storing)  (S3, RDS, Dynamodb)
- Protecting: 🕰 Soon...stay tuned (KMS, S3, ACM)
- Publishing: 🕰 Soon...stay tuned (CloudFront, pre-signed URL, OAI)
- Monitoring: 🕰 Soon...stay tuned (CloudWatch, CloudTrail, FlowLogs)

In each knowledge area, we have ordered the Workout steps by difficulty. Example:
- 101-basic-vpc
- 102-basic-subnets
- 103-vpc-default-route-default-security-group
- 104-internet-access
- 105-nat-gateway
and so on...

Some steps rely on previous steps. In each directory, the `dep.txt` file lists the required dependencies. 
The shell commands to apply Terraform or Cloudformation stacks verify automatically the required dependencies. 
You will be prompted if a dependant workout has not been deployed.

### Pre-requisites

- Install ``jq`` : https://stedolan.github.io/jq/tutorial/
- Install ``AWS CLI`` : https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html 
- Install ``Terraform`` : https://learn.hashicorp.com/tutorials/terraform/install-cli
- Install ``Terragrunt``:  https://terragrunt.gruntwork.io/docs/getting-started/install
- Install ``Serverless Framework`` : https://www.serverless.com/framework/docs/getting-started

All workouts have been tested with:
- AWS CLI **1.17.2** or AWS CLI **2.2.10**
- Terraform **1.0.1** or Terraform **1.0.2**
- Terragrunt **0.36.1**
- Serverless Framework **2.3.0**
- Mac OS 10.15
- Ubuntu 20.04 LTS

## DISCLAIMER 

🔥🔥🔥   All these workouts execute on **YOUR AWS account**. Many of these workouts induce costs.

🔥🔥🔥   You are responsible to delete workouts and/or created resources, services or components at the end of the workout.
Otherwise, AWS will bill you...

🔥🔥🔥   We highly suggest you creating a budget and alerting in your AWS account. Follow this AWS [tutorial](https://docs.aws.amazon.com/cost-management/latest/userguide/budgets-create.html)


## LET'S START
### 🚀 Set up your AWS profile 🚀 
First follow [this documentation](./doc/install-aws.md) to set up a `aws-workout` AWS profile on your computer.
All shell commands provided in these tutorials require this profile.

### 🚀 How to perform Workouts ? 🚀 

You can perform Workouts with two modes:
- **Terraform**: you will see basics of **Terraform** and **Terragrunt** while learning AWS [intro terraform](https://www.terraform.io/intro/index.html)
- **AWS CloudFormation**: you will see basics of **CloudFormation** while learning AWS [intro cloudformation](https://aws.amazon.com/fr/cloudformation/getting-started/)
  
These workouts do not intend to show **Terraform** or **CloudFormation** best practices. 

There are very tiny differences between the two modes:
- Some workouts require components/services deployed in multiple regions. Terraform supports multi-region Stacks, whereas CloudFormation requires one stack per region.
- Terraform allows modifying default objects (routes...) whereas CloudFormation does not.

⚠️ YOU CAN NOT SWITCH FROM TERRAFORM TO CLOUDFORMATION (or reverse) ⚠️
- **Terraform** Workouts may require states from the previous ones (stored in S3 bucket). 
- **Cloudformation** Workouts may require Stack outputs from previous ones (stored in AWS CloudFormation Stacks). ️

### AWS EC2 key pairs
Some workouts will create EC2 that requires SSH key pairs to log-in.
Follow this tutorial to create and inject a keypair in AWS [Key Pair](./doc/keypair.md).

## Tutorials
### Terraform Workouts 
If you want to use TERRAFORM versions, please install [Terraform CLI](./doc/install-terraform.md). 

#### 🚧 To apply a Terraform Workout Step:
In order to apply a tutorial use the **run-tutorial.sh** command.

```shell
./run-tutorial.sh xxxx
./run-tutorial.sh ./1-networking/101-basic-vpc
./run-tutorial.sh ./1-networking/102-basic-subnets
...
```

Tutorials are **chain linked**. For example, **102-basic-subnets** requires **101-basic-vpc** tutorial. 
You are free to apply manually each tutorials in the right order OR you can rely on **terragrunt** to apply the dependencies for you.
E.g. if **101-basic-vpc** has not been applied manually, it will be automatically applied if you apply **102-basic-subnets** tutorial.

Once the components have been properly created in AWS, you can test some assertions .

```shell
./launch.sh ./xxxx/TEST-yyyy.sh
./launch.sh ./1-networking/101-basic-vpc/TEST-display-created-vpc.sh
./launch.sh ./1-networking/102-basic-subnets/TEST-display-created-subnets.sh
...
```

#### 🧹To delete a Terraform Workout and free AWS resources:
At the end of the workout step, and if the step is not required for the next ones, you should delete the created AWS components.
Otherwise, you will 💸💸💸 **PAY** 💸💸💸 for unused components or services.
```shell
./delete-tutorial.sh xxx
./delete-tutorial.sh ./1-networking/102-basic-subnet
./delete-tutorial.sh ./1-networking/101-basic-vpc
...
```

### CloudFormation Workouts 

#### 🚧 To apply a CloudFormation Workout:
Apply the CloudFormation stack using the following command:
```shell
./cf-run-tutorial.sh xxxx
./cf-run-tutorial.sh ./1-networking/101-basic-vpc
./cf-run-tutorial.sh ./1-networking/102-basic-subnets
...
```

In the AWS Console and check the progress of your CloudFormation Stack.

Once the components have been properly created in AWS, you can test some assertions 

```shell
./launch.sh ./xxxx/TEST-yyyy.sh
./launch.sh ./1-networking/101-basic-vpc/TEST-display-created-vpc.sh
./launch.sh ./1-networking/102-basic-subnets/TEST-display-created-subnets.sh
...
```

#### 🧹To delete a CloudFormation Workout and free AWS resources:
At the end of the workout step, and if the step is not required for the next ones, you should delete the created AWS components.
Otherwise, you will 💸💸💸 **PAY** 💸💸💸 for unused components or services.
```shell
./cf-delete-tutorial.sh xxx
./cf-delete-tutorial.sh ./1-networking/102-basic-subnet
./cf-delete-tutorial.sh ./1-networking/101-basic-vpc
...
```


👉 NB: all resources and components created using the provided stacks are tagged with a `Purpose` Tag.
You will be able to list all resources, using the AWS Tag Manager in AWS Console.

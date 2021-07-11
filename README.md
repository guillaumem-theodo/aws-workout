# 👨‍🎓 AWS Workout 👨‍🎓
## Small and Quick Hands-on on AWS basic principles ##

### 🏛 Tutorials organisation 🏛 

We have grouped Workout Steps by knowledge categories:
- Networking: [1-networking](./1-networking)   (VPC, subnets, security groups, peering, dns)
- Computing: [2-computing](./2-computing)  (EC2, ECS, Lambdas, ALB, Auto-scaling)
- Storing: 🕰 Soon...stay tuned (S3, RDS, Dynamodb)
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
- Install ``Serverless Framework`` : https://www.serverless.com/framework/docs/getting-started

All workouts have been tested with:
- AWS CLI **1.17.2** or AWS CLI **2.2.10**
- Terraform **1.0.1** or Terraform **1.0.2**
- Serverless Framework **2.3.0**
- Mac OS 10.15
- Ubuntu 20.04 LTS

### 🚀 Set up your AWS profile 🚀 
First follow [this documentation](./doc/install-aws.md) to set up a `aws-workout` AWS profile on your computer.
All shell commands provided in these tutorials require this profile.

### 🚀 How to perform Workouts ? 🚀 

You can perform Workouts with two modes:
- **Terraform**: you will see basics of **Terraform** while learning AWS [intro terraform](https://www.terraform.io/intro/index.html)
- **AWS CloudFormation**: you will see basics of **CloudFormation** while learning AWS [intro cloudformation](https://aws.amazon.com/fr/cloudformation/getting-started/)
  
These workouts do not intend to show **Terraform** or **CloudFormation** best practices. 

There are very tiny differences between the two modes:
- some workouts require components/services deployed in multiple regions. Terraform supports multi-region Stacks, whereas CloudFormation requires one stack per region.
- Terraform allows modifying default objects (routes...) whereas CloudFormation does not.

⚠️ But YOU CAN NOT SWITCH FROM TERRAFORM TO CLOUDFORMATION (or reverse) ⚠️
- **Terraform** Workouts may require states from the previous ones (stored in S3 bucket). 
- **Cloudformation** Workouts may require Stack outputs from previous ones (stored in AWS CloudFormation Stacks). ️

### 🔑🔑 Create a Key Pair for your EC2 🔑🔑
In order to work with and to log into the EC2, you need to create an SSH keypair.
A key pair is a pair of private and public keys.
You will need to have the private key stored on your laptop (in the Workout root directory).
The public key need to be stored in AWS EC2 KeyPair Service.

#### 🚧 Howto to create and store the keypair:
In the Workout root directory 
  
```shell
./generate-keypair.sh
```

It will generate the private and public key files
  - private key file named `aws-workout-key-pair.pem`. Must be stored on your laptop in the Workout root directory.
  - public key file named `aws-workout-key-pair.pub`. Will be automatically uploaded in AWS. 

It will create a keypair in AWS named `aws-workout-key` and upload the public part of the key from your laptop.

#### 🚧 Enable SSH Agent Forwarding

Many TEST files rely on SSH and SSH Agent Forwarding (from your laptop to EC2 then to other EC2). 
Agent Forwarding is a way to SSH from servers to servers using the same credentials.
You need to enable **SSH Agent Forwarding** and to add the private key.

⚠️ SSH Agent Forwarding is not a good practice on PROD environments.

1) Enable SSH Agent Forwarding
```bash
vim ~/.ssh/config

Add:
Host *
  ForwardAgent yes
  AddKeysToAgent yes
```

2) Add the private key in agent forwarding
```bash
ssh-add -k aws-workout-key-pair.pem
```

2) You can check if the agent fowarding is set up using following command
```bash
ssh-add -L
```

### Terraform Workouts 
If you want to use TERRAFORM versions, please install [Terraform CLI](./doc/install-terraform.md). 

#### 🚧 To apply a Terraform Workout Step:
First initialize the Terraform state for the Workout Step (in the Workout Root directory). 
  It will create the Terraform State (one per workout step) in the S3 bucket  
  
```shell
./init-tutorial.sh xxxx
./init-tutorial.sh ./1-networking/101-basic-vpc
./init-tutorial.sh ./1-networking/102-basic-subnets
...
``` 

NB: The `init-tutorial.sh` command creates a SYMLINK from `common\variables.tf` directory inside the workout step directory.
This way, the `main.tf` Terraform file can **inherits** variables and data (and thus reduce the complexity of the file).


Then, apply the Terraform plan on your AWS account 

```shell
./run-tutorial.sh xxxx
./run-tutorial.sh ./1-networking/101-basic-vpc
./run-tutorial.sh ./1-networking/102-basic-subnets
...
```

Once the components have been properly created in AWS, you can test some assertions 

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
./run-cf-tutorial.sh xxxx
./run-cf-tutorial.sh ./1-networking/101-basic-vpc
./run-cf-tutorial.sh ./1-networking/102-basic-subnets
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
./delete-cf-tutorial.sh xxx
./delete-cf-tutorial.sh ./1-networking/102-basic-subnet
./delete-cf-tutorial.sh ./1-networking/101-basic-vpc
...
```


👉 NB: all resources and components created using the provided stacks are tagged with a `Purpose` Tag.
You will be able to list all resources, using the AWS Tag Manager in AWS Console.

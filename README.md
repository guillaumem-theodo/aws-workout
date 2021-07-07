# 👨‍🎓 AWS Workout 👨‍🎓
## Small and Quick Hands-on on AWS basic principles ##

### 🚀 Set up your AWS profile 🚀 
First follow [this documentation](./doc/install-aws.md) to set up a `aws-workout` AWS profile on your computer.
All shell commands provided in these tutorials require this profile.

### 🏛 Tutorials organisation 🏛 

We have grouped Workout Steps by knowledge categories:
- Networking: [1-networking](./1-networking)   (VPC, subnets, security groups, peering, dns)
- Computing: 🚧 Under construction  (EC2, ECS, Lambdas, ALB, Auto-scaling)
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

### 🚀 How to apply Workouts ? 🚀 

You can execute Workouts with two modes:
- **Terraform**: you will learn **Terraform** while learning AWS [intro terraform](https://www.terraform.io/intro/index.html)
- **AWS CloudFormation**: you will learn **CloudFormation** while learning AWS [intro cloudformation](https://aws.amazon.com/fr/cloudformation/getting-started/)
  
It's your choice...

There are very tiny differences between the two modes. 

⚠️ But YOU CAN NOT SWITCH FROM TERRAFORM TO CLOUDFORMATION (or reverse) ⚠️
- **Terraform** Workouts may require states from the previous ones (stored in S3 bucket). 
- **Cloudformation** Workouts may require Stack outputs from previous ones (stored in AWS CloudFormation Stacks). ️

### 🔑🔑 Create a Key Pair for your EC2 🔑🔑
In order to work with and to log into the EC2, you need to create a keypair.
A key pair is a pair of private and public key.
You will need to have the private key on your laptop.
The public key need to be stored in AWS EC2 KeyPair Service.

🚧 Howto to create and store the keypair:
In the Workout root directory 
  
```shell
./generate-keypair.sh
```

It will create a keypair in AWS named `aws-workout-key`
It will create the private and public key
  - private key file named `aws-workout-key-pair.pem`. Must be stored on your laptop in the Workout root directory.
  - public key file named `aws-workout-key-pair.pub`. Will be automatically uploaded in AWS.

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

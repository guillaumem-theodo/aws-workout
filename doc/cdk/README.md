## CDK Workouts
If you want to use CDK framework, please install [CDK and Java](./install-cdk.md). 

#### 🚧 Init CDK
You need to bootstrap CDK.   
1️⃣ Setup in ``backend-env.conf`` file, the two regions (`TUTORIAL_REGION` and `TUTORIAL_ANOTHER REGION`)  you wish to use in workouts   
2️⃣ Setup in ``backend-env.conf`` file, a unique **key** for the S3 names (`TUTORIAL_UNIQUE_KEY`)   
3️⃣ Use ``./cdk-init.sh`` shell command to bootstrap CDK in two AWS regions.   

#### Conventions
All workouts follow these naming and coding conventions 👉 [here](../conventions.md)

#### 🚧 To apply a CDK Workout:
Apply the CDK stack using the following command:
```shell
./cdk-run-tutorial.sh xxxx
./cdk-run-tutorial.sh ./1-networking/101-basic-vpc
./cdk-run-tutorial.sh ./1-networking/102-basic-subnets
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

Assertions are shell scripts named `TEST-xxxxxx.sh`. They provide easy ways to test the tutorials. 
This shell scripts use AWS CLI or pure bash command to test deployed components.

#### 🧹To delete a CDK Workout and free AWS resources:
At the end of the workout step, and if the step is not required for the next ones, you should delete the created AWS components.
Otherwise, you will 💸💸💸 **PAY** 💸💸💸 for unused components or services.
```shell
./cdk-delete-tutorial.sh xxx
./cdk-delete-tutorial.sh ./1-networking/102-basic-subnet
./cdk-delete-tutorial.sh ./1-networking/101-basic-vpc
...
```

#### Dependencies
Tutorials are **chain linked**. For example, **102-basic-subnets** requires resources created in **101-basic-vpc** tutorial. 
You are free to apply manually each tutorial one at a time OR you can rely on **CDK** to apply the dependencies in the right order for you.
E.g. if **101-basic-vpc** has not been applied manually, CDK will be automatically applied it for you if you apply **102-basic-subnets** tutorial.

Tutorials are **chain linked**. E.g: When you destroy ``102-basic-subnet`` it will delete ``101-basic-vpc``

Dependencies are defined in ``CDK Stacks`` classes (Java classes).  
E.g: [here](../../cdk/src/main/java/gmi/workouts/networking/workout102/BasicSubnetsStack102.java)


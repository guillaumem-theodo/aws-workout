## Terraform Workouts 
If you want to use TERRAFORM framework, please install [Terraform and Terragrunt CLI](./install-terraform.md). 

1️⃣ Setup in ``backend-env.conf`` file, the two regions (`TUTORIAL_REGION` and `TUTORIAL_ANOTHER REGION`)  you wish to use in workouts  
2️⃣ Setup in ``backend-env.conf`` file, a unique **key** for the S3 names (`TUTORIAL_UNIQUE_KEY`)   

#### Conventions
All workouts follow these naming and coding conventions 👉 [here](../conventions.md)

#### 🚧 To apply a Terraform Workout Step:
In order to apply a workout use the **run-tutorial.sh** command.

```shell
./run-tutorial.sh xxxx
./run-tutorial.sh ./1-networking/101-basic-vpc
./run-tutorial.sh ./1-networking/102-basic-subnets
...
```

Once the components have been properly created in AWS, you can test some assertions .

```shell
./launch.sh ./xxxx/TEST-yyyy.sh
./launch.sh ./1-networking/101-basic-vpc/TEST-display-created-vpc.sh
./launch.sh ./1-networking/102-basic-subnets/TEST-display-created-subnets.sh
...
```

Assertions are shell scripts named `TEST-xxxxxx.sh`. They provide easy ways to test the tutorials. 
This shell scripts use AWS CLI or pure bash command to test deployed components.

#### 🧹To delete a Terraform Workout and free AWS resources:
At the end of the workout tutorial, and if the step is not required for the next ones, you must delete the created AWS components.
Otherwise, you will 💸💸💸 **PAY** 💸💸💸 for unused components or services.
```shell
./delete-tutorial.sh xxx
./delete-tutorial.sh ./1-networking/102-basic-subnet
./delete-tutorial.sh ./1-networking/101-basic-vpc
...
```

#### Dependencies
Tutorials are **chain linked**. For example, **102-basic-subnets** requires resources created in **101-basic-vpc** tutorial. 
You are free to apply manually each tutorial one at a time OR you can rely on **Terragrunt** to apply the dependencies in the right order for you.
E.g. if **101-basic-vpc** has not been applied manually, Terragrunt will be automatically applied it for you if you apply **102-basic-subnets** tutorial.

Tutorials are **chain linked**. E.g: When you destroy ``102-basic-subnet`` it will delete ``101-basic-vpc``

Dependencies are defined in ``terragrunt.hcl`` files in ``dependency`` tags.  
E.g: [here](../../1-networking/102-basic-subnets/terragrunt.hcl)


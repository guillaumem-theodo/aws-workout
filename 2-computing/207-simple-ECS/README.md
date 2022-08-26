## ECS - FARGATE : Managed Cluster

⭐⭐⭐ (more complex)️ ⭐⭐⭐

ECS is an AWS Service for Containers Clusters

The **ECS** Service let you provision Tasks and Services.
- **Tasks** are descriptions of the containers to be run
- **Services** are definitions of the desired instances count started from tasks

Services and Containers are run on an **ECS Cluster**.

There are two types of ECS Cluster:
- **EC2** based one. You provision the EC2s underlying the ECS cluster
- **FARGATE** based ECS Cluster; AWS provisions the workers underlying the ECS Cluster.

## Your mission

1️⃣ As in Workout `206 on auto-scaling`:
 - Create a public subnet, and a public security group (in order to create a public facing ALB)
 - Create a private subnet, and a private security group (in order to host ECS workers)
 - Add an Internet Gateway and a public route to internet, associated with the public subnet
 - Add a NAT Gateway (in the public subnet), and a route to internet. Associated it to the private subnet
 - Create an ALB and ALB Target Group 👉 BUT WITHOUT 👈 any target
   - Targets will be referenced by IP (and no more by instanceId)

2️⃣ Create an ECS Cluster. 
    - Select `FARGATE` container type. Fargate is a fully managed version of ECS.  You will NOT have to create EC2 to support ECS cluster. 
  
3️⃣ Create an `ECS Task Definition`
  - The task Definition lets you select:
      - the Docker Image (from DockerHub: the private subnet needs access to internet to download image --> NAT gateway), 
        For example, you can select ``httpd:2.4`` Apache image
      - the cpu size (256 means 1/4 V-CPU),
      - the Ram size (512 MB)
  - The networking mode of the Task will be 'awsvpc' (which means that the docker container share the VPC network)
    (see this [doc](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-networking-awsvpc.html))

4️⃣ Create an `ECS Service`
  - The service describes how many PODs will be created
  - The service sets the minimum and maximum number of PODs instances
  - The service describes which Task to start (select the Task Definition)
  - 👉 The service must be linked to the ALB TargetGroup
  - 👉 The service must be created AFTER the ALB

### Organise your files (CDK and Terraform Tutorials)
As in previous workouts, create files or Stacks to separate domains.

<div align="center">
<img src="./doc/207-simple-ECS.png" width="800" alt="ECS">
</div>
<br>

## Your success
🏁 Test that the ALB serves the HTTP page
- ✅ Check that the CURL on the ALB URL is served by different workers.

- You can use following commands to check your mission success
  ```shell
  ./launch.sh 2-computing/207-simple-ECS/TEST-load-balancer.sh
  ```

## Materials
[Doc AWS](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/Welcome.html)





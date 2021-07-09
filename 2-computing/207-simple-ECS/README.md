## ECS - FARGATE : Managed Cluster

⭐⭐⭐ (more complexe)️ ⭐⭐⭐

- 1️⃣ As in Workout `206 on auto-scaling`:
    - Create a public subnet, and a public security group
    - Create a private subnet, and a private security group
    - Add an Internet Gateway and a public route to internet, associated with the public subnet
    - Add a NAT Gateway (in the public subnet), and a route to internet. Associated it to the private subnet
    - Create an ALB and ALB Target Group BUT WITHOUT any target
  
  
- 2️⃣ Create an ECS Cluster. Select `FARGATE` container type. Fargate is a fully managed version of ECS. 
  You will NOT have to create EC2 to support ECS cluster. 
  
- 3️⃣ Create an `ECS Task Definition`
  - The task Definition let you select:
      - the Docker Image (from DockerHub: the private subnet needs access to internet to download image), 
      - the cpu size (256 means 1/4 V-CPU),
      - the Ram size (512 MB)
  - The networking mode of the Task will be 'awsvpc' (which means that the docker container share the VPC network)
- Create an `ECS Service`
  - The service describes how many PODs will be created
  - The service sets the minimum and maximum number of PODs instances
  - The service describes which Task to start (select the Task Definition)
  - The service must be linked to the ALB TargetGroup
  

![Image of VPC](./doc/207-simple-ECS.png)





## ASG - Auto Scaling Group

⭐⭐⭐ (more complex)️ ⭐⭐⭐

Instead of creating a fixed finite number of workers, like in `205-alb` workout, we are going to ask AWS to start or stop workers depending on the traffic load.

The ALB target Group will not be linked anymore to EC2 Targets but to an *Auto Scaling Group* (ASG).

An ASG is made of:
- **a launch template** or **a launch configuration**: definition of the type of EC2 to start (cpu, ram, AMI...) - **WHAT** TO LAUNCH
- **scaling options**: definition of the desired number min/max of instances, conditions to start/stop,... - **WHEN** TO LAUNCH

👉 From the 101-basic-vpc and 102-basic-subnets AND 206-alb

You will have to combine many topics seen in previous exercises.

1️⃣ Like in `Workout 205`, create a Bastion Architecture,
- with a public subnet / sg for ALB (HTTP from everywhere)
- with a limited subnet / sg for Bastion (SSH access from your IP only)
- with a private subnet / sg for workers (HTTP from ALB subnet, SSH from bastion subnet)

👉 BUT do not create manually the three EC2 workers !!!

2️⃣ Instead of a finite number of workers, 
- create a `launch template`, that describe how your EC2 will be created dynamically
- create an `Auto Scaling Group` (ASG) that reference the launch template`
- register the ASG inside the Application Load Balancer

🏁 Test that the load is handled by different workers
- ✅ Check that the CURL on the ALB URL is served by different workers.
- ✅ Stop manually an EC2 (one of the worker), observe that the ASG restart a new one

[Doc AWS](https://docs.aws.amazon.com/autoscaling/ec2/userguide/AutoScalingGroup.html)


![Image of VPC](./doc/206-auto-scaling.png)





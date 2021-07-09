######################################################################################
## ECS
resource "aws_ecs_cluster" "ecs-cluster-207" {
  name = "cpu-207-ecs-cluster"
  capacity_providers = ["FARGATE"]
  tags = {
    Purpose: var.dojo
  }
}

resource "aws_ecs_task_definition" "ecs-task-definition-207" {
  family = "cpu-207-task-definition"
  requires_compatibilities = ["FARGATE"]
  network_mode = "awsvpc"
  cpu = 256
  memory = 512

  container_definitions = jsonencode([
    {
      name      = "cpu-207-container-httpd"
      image     = "httpd:2.4"  ## Name of the image in DockerHub

      essential = true
      portMappings = [
        {
          containerPort = 80
          hostPort      = 80
        }
      ]
    }
  ])
}

resource "aws_ecs_service" "ecs-service-207" {
  depends_on = [aws_lb_target_group.my_alb_target_group]
  name = "cpu-207-ecs-service"
  cluster = aws_ecs_cluster.ecs-cluster-207.id
  task_definition = aws_ecs_task_definition.ecs-task-definition-207.id
  desired_count = 3
  launch_type = "FARGATE"

  network_configuration {
    subnets = [data.terraform_remote_state.subnets-102.outputs.net-102-subnet-3-id]
    security_groups = [aws_security_group.sg-207-private.id]
  }

  load_balancer {
    container_name = "cpu-207-container-httpd"
    container_port = 80
    target_group_arn = aws_lb_target_group.my_alb_target_group.arn
  }
}

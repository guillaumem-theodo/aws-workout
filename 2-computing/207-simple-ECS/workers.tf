######################################################################################
## ECS
resource "aws_ecs_cluster" "cpu-207-ecs-cluster" {
  name = "cpu-207-ecs-cluster"
  tags = {
    Name: "cpu-207-ecs-cluster"
    Purpose: var.dojo
  }
}

resource "aws_ecs_cluster_capacity_providers" "cpu-207-ecs-cluster-capacity-provider" {
  cluster_name = aws_ecs_cluster.cpu-207-ecs-cluster.name

  capacity_providers = ["FARGATE"]

  default_capacity_provider_strategy {
    base              = 1
    weight            = 100
    capacity_provider = "FARGATE"
  }
}

resource "aws_ecs_task_definition" "cpu-207-ecs-task-definition" {
  family = "cpu-207-ecs-task-definition"
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

resource "aws_ecs_service" "cpu-207-ecs-service" {
  depends_on = [aws_lb_target_group.cpu-207-alb-target-group]
  name = "cpu-207-ecs-service"
  cluster = aws_ecs_cluster.cpu-207-ecs-cluster.id
  task_definition = aws_ecs_task_definition.cpu-207-ecs-task-definition.id
  desired_count = 3
  launch_type = "FARGATE"

  network_configuration {
    subnets = [var.subnet3_102_id]
    security_groups = [aws_security_group.cpu-207-sg-2-private.id]
  }

  load_balancer {
    container_name = "cpu-207-container-httpd"
    container_port = 80
    target_group_arn = aws_lb_target_group.cpu-207-alb-target-group.arn
  }
}

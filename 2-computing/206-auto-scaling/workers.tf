######################################################################################
## WORKERS TEMPLATE
resource "aws_launch_template" "launch-template-206" {
  name_prefix   = "cpu-206-ec2-"
  image_id      = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  user_data = filebase64("ec2-apache-install.sh")

  network_interfaces {
    subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-3-id
    device_index = 0
    associate_public_ip_address = false
    security_groups = [aws_security_group.sg-206-private.id]
  }

  tags = {
    Purpose: var.dojo
  }
}

######################################################################################
## WORKERS TEMPLATE
resource "aws_launch_template" "cpu-206-launch-template" {
  name_prefix   = "cpu-206-ec2-"
  image_id      = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  user_data = filebase64("ec2-apache-install.sh")

  network_interfaces {
    subnet_id = var.subnet3_102_id
    device_index = 0
    associate_public_ip_address = false
    security_groups = [aws_security_group.cpu-206-sg-3-private.id]
  }

  tags = {
    Purpose: var.dojo
    Name: "cpu-206-launch-template"
  }

  depends_on = [aws_nat_gateway.cpu-206-nat-gtw] // Needed for upload httpd

}

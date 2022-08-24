########################################################################################################################
variable "vpc_id" {
  type = string
}

variable "subnet_102_id" {
  type = string
}


## CREATE an EC2 inside the subnet (with the associated route table) and inside the security group
## As a consequence the EC2 should be reachable (ping and SSH) from internet
## And EC2 can initiate traffic to internet (curl...)
resource "aws_instance" "cpu-203-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = var.subnet_102_id
  vpc_security_group_ids = [aws_security_group.cpu-203-sg-1.id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-203-ec2-1"
  }
}

output "cpu-203-ec2-1-public-ip" {
  value = aws_instance.cpu-203-ec2-1.public_ip
}
output "cpu-203-sg-id" {
  value = aws_security_group.cpu-203-sg-1.id
}
output "cpu-203-rt-id" {
  value = aws_route_table.cpu-203-rt.id
}
output "cpu-203-ec2-1-id" {
  value = aws_instance.cpu-203-ec2-1.id
}


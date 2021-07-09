######################################################################################
## WORKERS
resource "aws_instance" "worker-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = false
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-3-id
  security_groups = [aws_security_group.sg-205-private.id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-ec2-1"
  }
}

resource "aws_instance" "worker-ec2-2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = false
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-3-id
  security_groups = [aws_security_group.sg-205-private.id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-ec2-2"
  }
}

resource "aws_instance" "worker-ec2-3" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = false
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-4-id
  security_groups = [aws_security_group.sg-205-private.id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-ec2-3"
  }
}

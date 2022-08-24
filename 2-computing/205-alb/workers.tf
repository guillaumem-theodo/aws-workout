######################################################################################
## WORKERS
resource "aws_instance" "cpu-205-ec2-worker-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = false
  subnet_id = var.subnet3_102_id
  vpc_security_group_ids = [aws_security_group.cpu-205-sg-3.id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-ec2-1"
  }
}

resource "aws_instance" "cpu-205-ec2-worker-2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = false
  subnet_id = var.subnet3_102_id
  vpc_security_group_ids = [aws_security_group.cpu-205-sg-3.id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-ec2-2"
  }
}

resource "aws_instance" "cpu-205-ec2-worker-3" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = false
  subnet_id = var.subnet4_102_id
  vpc_security_group_ids = [aws_security_group.cpu-205-sg-3.id]
  key_name = "aws-workout-key"
  user_data = file("ec2-apache-install.sh")

  tags = {
    Purpose: var.dojo
    Name: "cpu-205-ec2-3"
  }
}

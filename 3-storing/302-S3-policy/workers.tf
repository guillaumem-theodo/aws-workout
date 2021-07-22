######################################################################################
resource "aws_instance" "worker-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
  security_groups = [aws_security_group.sg-302-public.id]
  key_name = "aws-workout-key"
  iam_instance_profile = aws_iam_instance_profile.instance-profile.id

  tags = {
    Purpose: var.dojo
    Name: "sto-302-ec2-1"
  }
}

resource "aws_instance" "worker-ec2-2" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = data.terraform_remote_state.subnets-102.outputs.net-102-subnet-1-id
  security_groups = [aws_security_group.sg-302-public.id]
  key_name = "aws-workout-key"
  iam_instance_profile = aws_iam_instance_profile.instance-profile.id
  tags = {
    Purpose: var.dojo
    Name: "sto-302-ec2-2"
  }
}

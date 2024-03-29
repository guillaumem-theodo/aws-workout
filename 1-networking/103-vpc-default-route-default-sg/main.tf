########################################################################################################################
variable "vpc_id" {
  type = string
}

variable "subnet_102_id" {
  type = string
}

######################################################################################
## CREATES one EC2 in subnet to show Default Routes
######################################################################################
resource "aws_instance" "net-103-ec2-1" {
  ami = data.aws_ami.amazon-linux.image_id
  instance_type = "t2.micro"
  associate_public_ip_address = true
  subnet_id = var.subnet_102_id
  key_name = "aws-workout-key"

  tags = {
    Purpose: var.dojo
    Name: "net-103-ec2-1"
  }
}


########################################################################################################################
## OUTPUTS FOR FOLLOWING TUTORIALS
########################################################################################################################
output "net-103-ec2-1-id" {
  value = aws_instance.net-103-ec2-1.id
}
output "net-103-ec2-1-public-ip" {
  value = aws_instance.net-103-ec2-1.public_ip
}

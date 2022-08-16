
######################################################################################
## 101 - Basic VPC
## Let's create a VPC !!!
## A VPC is a private cloud.
## - A set private IP (IPv4) addresses that will be available for your systems
## - CIDR specified at VPC creation defines the template of your private IP addresses
## - CIDR can rely on RFC 1918
## - A VPC is in ONE AWS Region (e.g. eu-west-2). Region is selected with the AWS provider used with Terraform
######################################################################################
resource "aws_vpc" "net-101-vpc" {

  ## RFC 1918 Blocks of IP addresses
  ## You can use https://www.ipaddressguide.com/cidr to see and compute blocks of Ips

  //cidr_block = "10.0.0.0/16"  // You will get nearly 65536 IP private addresses all starting with 10.0.x.x

  ## Examples of RFC 1918 Blocks of IP addresses - Recommended
  cidr_block = "10.1.0.0/16"  // You will get nearly 65536 IP private addresses all starting with 10.1.x.x
  //cidr_block = "10.1.2.0/24"  // You will get nearly 256 IP private addresses all starting with 10.1.2.x
  //cidr_block = "172.16.0.0/16"  // You will get nearly 65536 IP private addresses all starting with 172.16.x.x
  //cidr_block = "192.168.0.0/24"  // You will get nearly 256 IP private addresses all starting with 192.168.0.x
  //cidr_block = "192.168.0.0/28"  // You will get nearly 16 IP private addresses all starting with 192.168.0.x

  ## Non RFC 1918 Blocks of IP addresses - Not recommended
  ## Non RFC 1918 Ips are PUBLIC IPs and thus may be used by others and thus may conflict
  //cidr_block = "30.34.0.0/16"  // You will get nearly 65536 IP private addresses all starting with 30.34.x.x

  tags = {
    Purpose: var.dojo
    Name: "net-101-vpc"
  }
}

########################################################################################################################
## OUTPUTS FOR FOLLOWING TUTORIALS
########################################################################################################################
output "net-101-vpc-id" {
  value = aws_vpc.net-101-vpc.id
}

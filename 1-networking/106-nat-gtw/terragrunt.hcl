
dependency "vpc" {
  config_path = "../101-basic-vpc"
}
dependency "subnets" {
  config_path = "../102-basic-subnets"
}
dependency "bastion" {
  config_path = "../105-bastion"
}

inputs = {
  vpc_id = dependency.vpc.outputs.net-101-vpc-id
  public_subnet_102_id = dependency.subnets.outputs.net-102-subnet-1-id
  private_subnet_102_id = dependency.subnets.outputs.net-102-subnet-2-id
  private_route_table_105_id = dependency.bastion.outputs.net-105-rt-2-id
  private_security_group_105_id = dependency.bastion.outputs.net-105-sg-2-id
}

include "root" {
  path = find_in_parent_folders()
}




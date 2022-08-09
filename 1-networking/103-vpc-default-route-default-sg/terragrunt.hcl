
dependency "vpc" {
  config_path = "../101-basic-vpc"
}
dependency "subnets" {
  config_path = "../102-basic-subnets"
}

inputs = {
  vpc_id = dependency.vpc.outputs.net-101-vpc-id
  subnet_102_id = dependency.subnets.outputs.net-102-subnet-2-id
}

include "root" {
  path = find_in_parent_folders()
}




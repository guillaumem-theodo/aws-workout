
dependency "vpc" {
  config_path = "../101-basic-vpc"
}

inputs = {
  vpc_id = dependency.vpc.outputs.net-101-vpc-id
}

include "root" {
  path = find_in_parent_folders()
}




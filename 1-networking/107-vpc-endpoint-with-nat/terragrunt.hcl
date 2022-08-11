
dependency "vpc" {
  config_path = "../101-basic-vpc"
}
dependency "subnets" {
  config_path = "../102-basic-subnets"
}
dependency "nat" {
  config_path = "../106-nat-gtw"
}
dependency "buckets" {
  config_path = "../../common/buckets"
}
dependency "bastion" {
  config_path = "../105-bastion"
}

inputs = {
  vpc_id = dependency.vpc.outputs.net-101-vpc-id
  private_route_table_105_id = dependency.bastion.outputs.net-105-rt-2-id
}

include "root" {
  path = find_in_parent_folders()
}




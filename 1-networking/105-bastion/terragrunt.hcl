
dependency "vpc" {
  config_path = "../101-basic-vpc"
}
dependency "subnets" {
  config_path = "../102-basic-subnets"
}
dependency "iam" {
  config_path = "../../common/iam"
}

inputs = {
  vpc_id = dependency.vpc.outputs.net-101-vpc-id
  public_subnet_102_id = dependency.subnets.outputs.net-102-subnet-1-id
  private_subnet_102_id = dependency.subnets.outputs.net-102-subnet-2-id
  ec2_profile_instance_id = dependency.iam.outputs.iam_ec2_instance_profile_id
}

include "root" {
  path = find_in_parent_folders()
}




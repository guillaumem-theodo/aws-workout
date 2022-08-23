include "root" {
  path = find_in_parent_folders()
}

dependency "vpc" {
  config_path = "../../1-networking/101-basic-vpc"
}
dependency "subnets" {
  config_path = "../../1-networking/102-basic-subnets"
}
dependency "iam" {
  config_path = "../../common/iam"
}
dependency "buckets" {
  config_path = "../../common/buckets"
}

inputs = {
  vpc_id = dependency.vpc.outputs.net-101-vpc-id
  subnet_102_id = dependency.subnets.outputs.net-102-subnet-2-id
  ec2_profile_instance_id = dependency.iam.outputs.iam_ec2_instance_profile_id
}

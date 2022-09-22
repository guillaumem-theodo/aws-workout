include "root" {
  path = find_in_parent_folders()
}

dependency "vpc" {
  config_path = "../../1-networking/101-basic-vpc"
}
dependency "subnets" {
  config_path = "../../1-networking/102-basic-subnets"
}
dependency "buckets" {
  config_path = "../../common/buckets"
}

inputs = {
  vpc_id = dependency.vpc.outputs.net-101-vpc-id
  subnet1_102_id = dependency.subnets.outputs.net-102-subnet-1-id
  subnet2_102_id = dependency.subnets.outputs.net-102-subnet-2-id
  s3_bucket1 = dependency.buckets.outputs.common_s3_bucket_1_name
  s3_bucket2 = dependency.buckets.outputs.common_s3_bucket_2_name
}

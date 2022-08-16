package gmi.workouts.networking.workout102;

import gmi.workouts.networking.workout101.VpcStack101;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.amazon.awscdk.services.ec2.CfnVPC;
import software.constructs.Construct;

import java.util.List;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

/*
######################################################################################
## SUBNETS
######################################################################################
## 1) A Subnet is a subset of private IP addresses of the VPC CIDR block
## The subnet CIDR block indicates the range of IP addresses that can be used in the subnet
## By definition the subnet CIDR block is included in the VPC CIDR block  (i.e. the /x of the subnet CIDR is greater than the /y of the VPC CIDR)
## 2) A Subnet spans in ONE Availability Zone (one AZ of the VPC Region AZs)
## 3) Subnets CIDR of the same VPC can't overlap
 */
public class BasicSubnetsStack102 extends Stack {

    private final CfnSubnet subnet2;
    private final CfnSubnet subnet1;

    public BasicSubnetsStack102(final Construct scope, final String id, final StackProps props, VpcStack101 vpcStack101) {
        super(scope, id, props);
        addDependency(vpcStack101);

        List<String> availabilityZones = Stack.of(this).getAvailabilityZones(); // easy way to get all AZ identifiers of the current region
        String oneAZ = availabilityZones.get(0);
        String anotherAZ = availabilityZones.get(1);

        CfnVPC vpc = vpcStack101.getVpc();
        // Create 4 subnets (in two AZ)
        //## This first SUBNET lies in the first AZ of the Region and has (256) IPs from 10.1.0.0 to 10.1.0.255

        subnet1 = CfnSubnet.Builder.create(this, "net-102-subnet-1")
                .cidrBlock("10.1.0.0/24")
                .vpcId(vpc.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-102-subnet-1")).build();

        // ## This second SUBNET lies in the second AZ of the Region and has (256) IPs from 10.1.1.0 to 10.1.1.255
        subnet2 = CfnSubnet.Builder.create(this, "net-102-subnet-2")
                .cidrBlock("10.1.1.0/24")
                .vpcId(vpc.getAttrVpcId())
                .availabilityZone(anotherAZ)
                .tags(createCommonTags("net-102-subnet-2")).build();

        // ## This third SUBNET lies in the first AZ of the Region and has (4096) IPs from 10.1.224.0 to 10.1.239.255
        CfnSubnet.Builder.create(this, "net-102-subnet-3")
                .cidrBlock("10.1.224.0/20")
                .vpcId(vpc.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-102-subnet-3")).build();

        // ## This fourth SUBNET lies in the second AZ of the Region and has (4096) IPs from 10.1.240.0 to 10.1.255.255
        CfnSubnet.Builder.create(this, "net-102-subnet-4")
                .cidrBlock("10.1.240.0/20")
                .vpcId(vpc.getAttrVpcId())
                .availabilityZone(anotherAZ)
                .tags(createCommonTags("net-102-subnet-4")).build();

    }

    public CfnSubnet getSubnet1() {
        return subnet1;
    }

    public CfnSubnet getSubnet2() {
        return subnet2;
    }

}

package gmi.workouts.networking.workout102;

import gmi.workouts.networking.workout101.VpcStack;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.amazon.awscdk.services.ec2.CfnVPC;
import software.constructs.Construct;

import java.util.List;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class BasicSubnetsStack extends Stack {

    private final CfnSubnet subnet1;
    private final CfnSubnet subnet2;
    private final CfnSubnet subnet3;
    private final CfnSubnet subnet4;

    public BasicSubnetsStack(final Construct scope, final String id, final StackProps props, VpcStack vpcStack) {
        super(scope, id, props);
        addDependency(vpcStack);

        List<String> availabilityZones = Stack.of(this).getAvailabilityZones(); // easy way to get all AZ identifiers of the current region
        String oneAZ = availabilityZones.get(0);
        String anotherAZ = availabilityZones.get(1);

        CfnVPC vpc = vpcStack.getVpc();
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
        subnet3 = CfnSubnet.Builder.create(this, "net-102-subnet-3")
                .cidrBlock("10.1.224.0/20")
                .vpcId(vpc.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-102-subnet-3")).build();

        // ## This fourth SUBNET lies in the second AZ of the Region and has (4096) IPs from 10.1.240.0 to 10.1.255.255
        subnet4 = CfnSubnet.Builder.create(this, "net-102-subnet-4")
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

    public CfnSubnet getSubnet3() {
        return subnet3;
    }

    public CfnSubnet getSubnet4() {
        return subnet4;
    }
}

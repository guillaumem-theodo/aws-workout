package gmi.workouts.networking.workout106;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.networking.workout105.BastionStack105;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class NatGatewayStack106 extends Stack {

    public NatGatewayStack106(final Construct scope, final String id, final StackProps props,
                              VpcStack101 vpcStack101,
                              BasicSubnetsStack102 subnetsStack102,
                              BastionStack105 bastionStack105) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(subnetsStack102);
        addDependency(bastionStack105);

        CfnSubnet publicSubnet = subnetsStack102.getSubnet1();
        CfnRouteTable privateRouteTable = bastionStack105.getPrivateRouteTable();

        CfnEIP cfnEIP = CfnEIP.Builder.create(this, "net-106-eip")
                .tags(createCommonTags("net-106-eip"))
                .build();

        CfnNatGateway natGateway = CfnNatGateway.Builder.create(this, "net-106-nat-gtw")
                .subnetId(publicSubnet.getAttrSubnetId())
                .allocationId(cfnEIP.getAttrAllocationId())
                .tags(createCommonTags("net-106-nat-gtw"))
                .build();

        CfnRoute.Builder.create(this, "net-106-route-1")
                .destinationCidrBlock("0.0.0.0/0")
                .natGatewayId(natGateway.getRef())
                .routeTableId(privateRouteTable.getAttrRouteTableId())
                .build();

    }
}

package gmi.workouts.networking.workout106;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import gmi.workouts.networking.workout105.BastionStack;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class NatGatewayStack extends Stack {

    public NatGatewayStack(final Construct scope, final String id, final StackProps props,
                           final VpcStack vpcStack,
                           final BasicSubnetsStack subnetsStack,
                           final BastionStack bastionStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(subnetsStack);
        addDependency(bastionStack);

        CfnSubnet publicSubnet = subnetsStack.getSubnet1();
        CfnRouteTable privateRouteTable = bastionStack.getPrivateRouteTable();

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

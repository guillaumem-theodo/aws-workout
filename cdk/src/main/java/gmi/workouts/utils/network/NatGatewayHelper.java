package gmi.workouts.utils.network;

import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class NatGatewayHelper {
    public static CfnNatGateway createAndAttachNatGateway(Construct scope, String name, CfnVPC vpc, CfnSubnet natSubnet,
                                                          CfnSubnet... privateSubnets) {
        CfnEIP cfnEIP = CfnEIP.Builder.create(scope, name + "-eip")
                .tags(createCommonTags(name + "-eip"))
                .build();

        CfnNatGateway natGateway = CfnNatGateway.Builder.create(scope, name + "-gtw")
                .subnetId(natSubnet.getAttrSubnetId())
                .allocationId(cfnEIP.getAttrAllocationId())
                .tags(createCommonTags(name + "-gtw"))
                .build();

        CfnRouteTable routeTable = CfnRouteTable.Builder.create(scope, name+ "-rt")
                .tags(createCommonTags(name))
                .vpcId(vpc.getAttrVpcId())
                .build();

        CfnRoute.Builder.create(scope, name + "-route-1")
                .destinationCidrBlock("0.0.0.0/0")
                .natGatewayId(natGateway.getRef())
                .routeTableId(routeTable.getAttrRouteTableId())
                .build();

        int count = 0;
        for (CfnSubnet privateSubnet : privateSubnets) {
            CfnSubnetRouteTableAssociation.Builder.create(scope, name + "-rt-association-subnet-" + count++)
                     .routeTableId(routeTable.getAttrRouteTableId())
                     .subnetId(privateSubnet.getAttrSubnetId())
                     .build();

        }
        return natGateway;
    }
}

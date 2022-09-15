package gmi.workouts.utils.network;

import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class InternetGatewayHelper {
    public static CfnInternetGateway createAndAttachInternetGateway(Construct scope, CfnVPC vpc, String name) {
        CfnInternetGateway internetGateway = CfnInternetGateway.Builder.create(scope, name)
                .tags(createCommonTags(name))
                .build();

        CfnVPCGatewayAttachment.Builder.create(scope, name + "-vpc-attachment")
                .vpcId(vpc.getAttrVpcId())
                .internetGatewayId(internetGateway.getAttrInternetGatewayId())
                .build();

        return internetGateway;
    }

    public static CfnRouteTable createAndAttachRouteTableToSubnets(Construct scope, String name, CfnVPC vpc,
                                                                   CfnInternetGateway igw, CfnSubnet... subnets) {
        CfnRouteTable routeTable = CfnRouteTable.Builder.create(scope, name)
                .tags(createCommonTags(name))
                .vpcId(vpc.getAttrVpcId())
                .build();

        if(igw != null) {
            CfnRoute.Builder.create(scope, name + "-route")
                    .destinationCidrBlock("0.0.0.0/0")
                    .gatewayId(igw.getAttrInternetGatewayId())
                    .routeTableId(routeTable.getAttrRouteTableId())
                    .build();
        }

        int count = 0;
        for (CfnSubnet subnet : subnets) {
            CfnSubnetRouteTableAssociation.Builder.create(scope, name + "-subnet-association-" + count++)
                    .routeTableId(routeTable.getAttrRouteTableId())
                    .subnetId(subnet.getAttrSubnetId())
                    .build();
        }

        return routeTable;
    }
}

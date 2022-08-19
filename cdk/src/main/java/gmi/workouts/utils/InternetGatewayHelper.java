package gmi.workouts.utils;

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

    public static CfnRouteTable createAndAttachRouteTableToSubnet(Construct scope, CfnVPC vpc, CfnSubnet subnet, CfnInternetGateway igw, String name) {
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

        CfnSubnetRouteTableAssociation.Builder.create(scope, name + "-subnet-association")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(subnet.getAttrSubnetId())
                .build();

        return routeTable;
    }
}

package gmi.workouts.networking.workout109;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.constructs.Construct;

import java.util.List;

import static gmi.workouts.common.CommonIAM.createCommonEC2InstanceProfile;
import static gmi.workouts.utils.EC2Helper.Ip.WITH_PUBLIC_IP;
import static gmi.workouts.utils.EC2Helper.createEC2;
import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachRouteTableToSubnets;
import static gmi.workouts.utils.SecurityGroupHelper.DefaultPort.SSH;
import static gmi.workouts.utils.SecurityGroupHelper.createSecurityGroup;
import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class VpcPeeringStack109 extends Stack {
    public VpcPeeringStack109(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


        CfnVPC vpc1 = CfnVPC.Builder.create(this, "net-109-vpc-1")
                .cidrBlock("10.0.0.0/16")
                .enableDnsHostnames(true)
                .enableDnsSupport(true)
                .tags(createCommonTags("net-109-vpc-1"))
                .build();

        CfnVPC vpc2 = CfnVPC.Builder.create(this, "net-109-vpc-2")
                .cidrBlock("10.1.0.0/16")
                .enableDnsHostnames(true)
                .enableDnsSupport(true)
                .tags(createCommonTags("net-109-vpc-2"))
                .build();

        CfnVPC vpc3 = CfnVPC.Builder.create(this, "net-109-vpc-3")
                .cidrBlock("10.2.0.0/16")
                .enableDnsHostnames(true)
                .enableDnsSupport(true)
                .tags(createCommonTags("net-109-vpc-3"))
                .build();

        List<String> availabilityZones = Stack.of(this).getAvailabilityZones(); // easy way to get all AZ identifiers of the current region
        String oneAZ = availabilityZones.get(0);

        CfnSubnet subnet1 = CfnSubnet.Builder.create(this, "net-109-subnet-1")
                .cidrBlock("10.0.0.0/24")
                .vpcId(vpc1.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-109-subnet-1")).build();

        CfnSubnet subnet2 = CfnSubnet.Builder.create(this, "net-109-subnet-2")
                .cidrBlock("10.1.0.0/24")
                .vpcId(vpc2.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-109-subnet-2")).build();

        CfnSubnet subnet3 = CfnSubnet.Builder.create(this, "net-109-subnet-3")
                .cidrBlock("10.2.0.0/24")
                .vpcId(vpc3.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-109-subnet-3")).build();

        CfnInternetGateway internetGateway = createAndAttachInternetGateway(this, vpc3, "net-109-igw");

        CfnRouteTable routeTable3 = createAndAttachRouteTableToSubnets(this, "net-109-rt-3", vpc3, internetGateway, subnet3);
        CfnRouteTable routeTable2 = createAndAttachRouteTableToSubnets(this, "net-109-rt-2", vpc2, null, subnet2);
        CfnRouteTable routeTable1 = createAndAttachRouteTableToSubnets(this, "net-109-rt-1", vpc1, null, subnet1);

        CfnSecurityGroup securityGroup1 = createSecurityGroup(this, vpc1, "net-109-sg-1");
        CfnSecurityGroup securityGroup2 = createSecurityGroup(this, vpc2, "net-109-sg-2");
        CfnSecurityGroup securityGroup3 = createSecurityGroup(this, vpc3, "net-109-sg-3", SSH);

        CfnSecurityGroupIngress.Builder.create(this, "net-109-sg-3-2")
                .groupId(securityGroup3.getAttrGroupId())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .cidrIp(vpc2.getCidrBlock())
                .build();

        CfnSecurityGroupIngress.Builder.create(this, "nnet-109-sg-2-3")
                .groupId(securityGroup2.getAttrGroupId())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .cidrIp(vpc3.getCidrBlock())
                .build();

        CfnSecurityGroupIngress.Builder.create(this, "net-109-sg-1-2")
                .groupId(securityGroup1.getAttrGroupId())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .cidrIp(vpc2.getCidrBlock())
                .build();

        CfnInstanceProfile instanceProfile = createCommonEC2InstanceProfile(this);
        createEC2(this, "net-109-ec2-1", subnet1, securityGroup1, WITH_PUBLIC_IP,
                        builder -> builder.iamInstanceProfile(instanceProfile.getInstanceProfileName()));
        createEC2(this, "net-109-ec2-2", subnet2, securityGroup2, WITH_PUBLIC_IP,
                        builder -> builder.iamInstanceProfile(instanceProfile.getInstanceProfileName()));
        createEC2(this, "net-109-ec2-3", subnet3, securityGroup3, WITH_PUBLIC_IP,
                        builder -> builder.iamInstanceProfile(instanceProfile.getInstanceProfileName()));

        createVpcPeering(vpc3, vpc2, routeTable3, routeTable2, securityGroup3, securityGroup2, "net-109-peering-3-2");

        CfnVPCPeeringConnection vpcPeering = createVpcPeering(vpc2, vpc1, routeTable2, routeTable1, securityGroup2, securityGroup1, "net-109-peering-2-1");

        new AllowVPCPeeringDNSResolution(this, "net-109-peering-2-1-options", vpcPeering);
    }

    private CfnVPCPeeringConnection createVpcPeering(CfnVPC vpcA, CfnVPC vpcB,
                                                     CfnRouteTable routeTableA, CfnRouteTable routeTableB,
                                                     CfnSecurityGroup securityGroupA, CfnSecurityGroup securityGroupB,
                                                     String name) {
        CfnVPCPeeringConnection peeringConnection = CfnVPCPeeringConnection.Builder.create(this, name)
                .vpcId(vpcA.getAttrVpcId())
                .peerVpcId(vpcB.getAttrVpcId())
                .tags(createCommonTags(name))
                .build();

        CfnRoute.Builder.create(this, name + "route-from-a-to-b")
                .routeTableId(routeTableA.getAttrRouteTableId())
                .destinationCidrBlock(vpcB.getCidrBlock())
                .vpcPeeringConnectionId(peeringConnection.getAttrId())
                .build();

        CfnRoute.Builder.create(this, name + "route-from-b-to-a")
                .routeTableId(routeTableB.getAttrRouteTableId())
                .destinationCidrBlock(vpcA.getCidrBlock())
                .vpcPeeringConnectionId(peeringConnection.getAttrId())
                .build();

        CfnSecurityGroupIngress.Builder.create(this, name + "sg-allow-access-to-A")
                .groupId(securityGroupB.getAttrGroupId())
                .cidrIp(vpcA.getCidrBlock())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .build();

        CfnSecurityGroupIngress.Builder.create(this, name + "sg-allow-access-to-B")
                .groupId(securityGroupA.getAttrGroupId())
                .cidrIp(vpcB.getCidrBlock())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .build();

        return peeringConnection;
    }

}

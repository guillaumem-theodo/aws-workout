package gmi.workouts.networking.workout109;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class VpcPeeringStack109 extends Stack {
    private static final String LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2 = "/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2";

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

        CfnInternetGateway internetGateway = createAndAttachInternetGateway(vpc3);
        CfnRouteTable routeTable3 = createAndAttachRouteTableToSubnet(vpc3, subnet3, internetGateway, "net-109-rt-3");
        CfnRouteTable routeTable2 = createAndAttachRouteTableToSubnet(vpc2, subnet2, null, "net-109-rt-2");
        CfnRouteTable routeTable1 = createAndAttachRouteTableToSubnet(vpc1, subnet1, null, "net-109-rt-1");

        CfnSecurityGroup securityGroup1 = createSecurityGroup(vpc1, "net-109-sg-1-ssh", false);
        CfnSecurityGroup securityGroup2 = createSecurityGroup(vpc2, "net-109-sg-2-ssh", false);
        CfnSecurityGroup securityGroup3WithSSH = createSecurityGroup(vpc3, "net-109-sg-3-ssh", true);

        createEC2(subnet1, securityGroup1, "net-109-ec2-1");
        createEC2(subnet2, securityGroup2, "net-109-ec2-2");
        createEC2(subnet3, securityGroup3WithSSH, "net-109-ec2-3");

        createVpcPeering(vpc3, vpc2, routeTable3, routeTable2, securityGroup3WithSSH, securityGroup2, "net-109-peering-3-2");
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



        CfnRoute.Builder.create(this, name + "route-one-way")
                .routeTableId(routeTableA.getAttrRouteTableId())
                .destinationCidrBlock(vpcB.getCidrBlock())
                .vpcPeeringConnectionId(peeringConnection.getAttrId())
                .build();

        CfnRoute.Builder.create(this, name + "route-other-way")
                .routeTableId(routeTableB.getAttrRouteTableId())
                .destinationCidrBlock(vpcA.getCidrBlock())
                .vpcPeeringConnectionId(peeringConnection.getAttrId())
                .build();

        CfnSecurityGroupIngress.Builder.create(this, name + "sg-one-way")
                .groupId(securityGroupB.getAttrGroupId())
                .cidrIp(vpcA.getCidrBlock())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .build();

        CfnSecurityGroupIngress.Builder.create(this, name + "sg-other-way")
                .groupId(securityGroupA.getAttrGroupId())
                .cidrIp(vpcB.getCidrBlock())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .build();

        return peeringConnection;
    }

    private CfnInternetGateway createAndAttachInternetGateway(CfnVPC vpc) {
        CfnInternetGateway internetGateway = CfnInternetGateway.Builder.create(this, "net-109-igw")
                .tags(createCommonTags("net-109-igw")).build();

        CfnVPCGatewayAttachment.Builder.create(this, "net-109-igw-vpc-attachment")
                .vpcId(vpc.getAttrVpcId())
                .internetGatewayId(internetGateway.getAttrInternetGatewayId())
                .build();

        return internetGateway;
    }

    private CfnRouteTable createAndAttachRouteTableToSubnet(CfnVPC vpc, CfnSubnet subnet, CfnInternetGateway igw, String name) {
        CfnRouteTable routeTable = CfnRouteTable.Builder.create(this, name)
                .tags(createCommonTags(name))
                .vpcId(vpc.getAttrVpcId())
                .build();

        if(igw != null) {
            CfnRoute.Builder.create(this, name + "-route")
                    .destinationCidrBlock("0.0.0.0/0")
                    .gatewayId(igw.getAttrInternetGatewayId())
                    .routeTableId(routeTable.getAttrRouteTableId())
                    .build();
        }

        CfnSubnetRouteTableAssociation.Builder.create(this, name + "-subnet-association")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(subnet.getAttrSubnetId())
                .build();

        return routeTable;
    }

    private CfnSecurityGroup createSecurityGroup(CfnVPC vpc, final String name, boolean withSSH) {
        List<CfnSecurityGroup.IngressProperty> securityGroupIngress;
        if(withSSH) {
            securityGroupIngress = Arrays.asList(
                    CfnSecurityGroup.IngressProperty.builder()
                            .cidrIp("0.0.0.0/0")
                            .ipProtocol("tcp").fromPort(22).toPort(22)
                            .build(),
                    CfnSecurityGroup.IngressProperty.builder()
                            .cidrIp("0.0.0.0/0")
                            .ipProtocol("icmp").fromPort(-1).toPort(-1)
                            .build()
            );
        }  else {
            securityGroupIngress = Collections.emptyList();
        }
        return CfnSecurityGroup.Builder.create(this, name)
                .vpcId(vpc.getAttrVpcId())
                .groupName(name)
                .groupDescription("Security Group " + name)
                .securityGroupEgress(Collections.singletonList(CfnSecurityGroup.EgressProperty.builder()
                        .cidrIp("0.0.0.0/0")
                        .ipProtocol("-1").fromPort(0).toPort(0)
                        .build()
                ))
                .securityGroupIngress(securityGroupIngress)
                .tags(createCommonTags(name)).build();
    }

    private void createEC2(CfnSubnet subnet, CfnSecurityGroup securityGroup, String name) {
        IMachineImage latestAMI = MachineImage.fromSsmParameter(LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2, null);
        CfnInstance.Builder.create(this, name)
                .imageId(latestAMI.getImage(this).getImageId())
                .keyName("aws-workout-key")
                .instanceType("t2.micro")
                .networkInterfaces(
                        Collections.singletonList(
                                CfnInstance.NetworkInterfaceProperty.builder()
                                        .subnetId(subnet.getAttrSubnetId())
                                        .associatePublicIpAddress(true)
                                        .groupSet(Collections.singletonList(securityGroup.getAttrGroupId()))
                                        .deviceIndex("0").build()

                        ))
                .tags(createCommonTags(name))
                .build();
    }

}

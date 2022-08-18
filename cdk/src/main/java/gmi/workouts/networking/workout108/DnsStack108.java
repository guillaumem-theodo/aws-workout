package gmi.workouts.networking.workout108;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gmi.workouts.networking.workout103.DefaultRouteAndSecurityGroupStack103.LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2;
import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class DnsStack108 extends Stack {
    public DnsStack108(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


        CfnVPC vpc1 = CfnVPC.Builder.create(this, "net-108-vpc-1")
                .cidrBlock("10.100.0.0/16")
                .enableDnsHostnames(true)
                .enableDnsSupport(true)
                .tags(createCommonTags("net-108-vpc-1"))
                .build();

        CfnVPC vpc2 = CfnVPC.Builder.create(this, "net-108-vpc-2")
                .cidrBlock("10.200.0.0/16")
                .enableDnsHostnames(false)
                .enableDnsSupport(false)
                .tags(createCommonTags("net-108-vpc-2"))
                .build();


        List<String> availabilityZones = Stack.of(this).getAvailabilityZones(); // easy way to get all AZ identifiers of the current region
        String oneAZ = availabilityZones.get(0);

        CfnSubnet subnet1 = CfnSubnet.Builder.create(this, "net-108-subnet-1")
                .cidrBlock("10.100.0.0/24")
                .vpcId(vpc1.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-108-subnet-1")).build();

        CfnSubnet subnet2 = CfnSubnet.Builder.create(this, "net-108-subnet-2")
                .cidrBlock("10.200.0.0/24")
                .vpcId(vpc2.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-108-subnet-2")).build();

        CfnInternetGateway internetGateway = createAndAttachInternetGateway(vpc1);
        createAndAttachRouteTableToSubnet(vpc1, subnet1, internetGateway);
        CfnSecurityGroup sg1 = createSecurityGroup(vpc1, "net-108-sg-1");
        CfnSecurityGroup sg2 = createSecurityGroup(vpc2, "net-108-sg-2");
        createEC2(subnet1, sg1, "net-108-ec2-1");
        createEC2(subnet2, sg2, "net-108-ec2-2");
    }

    private CfnInternetGateway createAndAttachInternetGateway(CfnVPC vpc) {
        CfnInternetGateway internetGateway = CfnInternetGateway.Builder.create(this, "net-108-igw")
                .tags(createCommonTags("net-108-igw")).build();

        CfnVPCGatewayAttachment.Builder.create(this, "net-108-igw-vpc-attachment")
                .vpcId(vpc.getAttrVpcId())
                .internetGatewayId(internetGateway.getAttrInternetGatewayId())
                .build();

        return internetGateway;
    }

    private void createAndAttachRouteTableToSubnet(CfnVPC vpc, CfnSubnet subnet, CfnInternetGateway igw) {
        CfnRouteTable routeTable = CfnRouteTable.Builder.create(this, "net-108-rt")
                .tags(createCommonTags("net-rt-108"))
                .vpcId(vpc.getAttrVpcId())
                .build();

        CfnRoute.Builder.create(this, "net-108-rt-1-internet")
                .destinationCidrBlock("0.0.0.0/0")
                .gatewayId(igw.getAttrInternetGatewayId())
                .routeTableId(routeTable.getAttrRouteTableId())
                .build();

        CfnSubnetRouteTableAssociation.Builder.create(this, "net-108-rt-association-subnet")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(subnet.getAttrSubnetId())
                .build();
    }

    private CfnSecurityGroup createSecurityGroup(CfnVPC vpc, final String name) {
        return CfnSecurityGroup.Builder.create(this, name)
                .vpcId(vpc.getAttrVpcId())
                .groupName(name)
                .groupDescription("Security Group with ingress for PING and SSH, and egress for all")
                .securityGroupEgress(Collections.singletonList(CfnSecurityGroup.EgressProperty.builder()
                        .cidrIp("0.0.0.0/0")
                        .ipProtocol("-1").fromPort(0).toPort(0)
                        .build()
                ))
                .securityGroupIngress(Arrays.asList(
                        CfnSecurityGroup.IngressProperty.builder()
                                .cidrIp("0.0.0.0/0")
                                .ipProtocol("tcp").fromPort(22).toPort(22)
                                .build(),
                        CfnSecurityGroup.IngressProperty.builder()
                                .cidrIp("0.0.0.0/0")
                                .ipProtocol("icmp").fromPort(-1).toPort(-1)
                                .build()
                ))
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

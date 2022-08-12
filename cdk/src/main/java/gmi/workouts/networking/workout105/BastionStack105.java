package gmi.workouts.networking.workout105;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class BastionStack105 extends Stack {

    public BastionStack105(final Construct scope, final String id, final StackProps props,
                           VpcStack101 vpcStack101,
                           BasicSubnetsStack102 subnetsStack102) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(subnetsStack102);

        CfnSubnet privateSubnet = subnetsStack102.getSubnet2();
        CfnSubnet publicSubnet = subnetsStack102.getSubnet1();

        CfnInternetGateway igw = createAndAttachInternetGateway(vpcStack101);

        createAndAttachRouteTableToPublicSubnet(vpcStack101, publicSubnet, igw);
        createAndAttachRouteTableToPrivateSubnet(vpcStack101, privateSubnet);

        CfnSecurityGroup publicSecurityGroup = createSecurityGroup(vpcStack101);
        CfnSecurityGroup privateSecurityGroup = createPrivateSecurityGroup(vpcStack101, publicSecurityGroup);

        createBastionEC2(publicSubnet, publicSecurityGroup);
        createPrivateEC2(privateSubnet, privateSecurityGroup);

    }

    @NotNull
    private CfnSecurityGroup createSecurityGroup(VpcStack101 vpcStack101) {
        return CfnSecurityGroup.Builder.create(this, "net-sg-105")
                .vpcId(vpcStack101.getVpc1().getAttrVpcId())
                .groupName("net-sg-105")
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
                .tags(createCommonTags("net-sg-105")).build();
    }

    @NotNull
    private CfnSecurityGroup createPrivateSecurityGroup(VpcStack101 vpcStack101, CfnSecurityGroup publicSecurityGroup) {
        return CfnSecurityGroup.Builder.create(this, "net-sg-private-105")
                .vpcId(vpcStack101.getVpc1().getAttrVpcId())
                .groupName("net-sg-private-105")
                .groupDescription("Security Group (private) with ingress from public security group only, egress ALL")
                .securityGroupEgress(Collections.singletonList(CfnSecurityGroup.EgressProperty.builder()
                        .cidrIp("0.0.0.0/0")
                        .ipProtocol("-1").fromPort(0).toPort(0)
                        .build()
                ))
                .securityGroupIngress(Collections.singletonList(
                        CfnSecurityGroup.IngressProperty.builder()
                                .sourceSecurityGroupId(publicSecurityGroup.getAttrGroupId())
                                .ipProtocol("-1").fromPort(0).toPort(0)
                                .build()
                ))
                .tags(createCommonTags("net-sg-private-105")).build();
    }
    private void createAndAttachRouteTableToPublicSubnet(VpcStack101 vpcStack101, CfnSubnet publicSubnet, CfnInternetGateway igw) {
        CfnRouteTable routeTable = CfnRouteTable.Builder.create(this, "public-route-table-105")
                .tags(createCommonTags("public-route-table-105"))
                .vpcId(vpcStack101.getVpc1().getAttrVpcId())
                .build();

        CfnRoute.Builder.create(this, "public-route-105")
                .destinationCidrBlock("0.0.0.0/0")
                .gatewayId(igw.getAttrInternetGatewayId())
                .routeTableId(routeTable.getAttrRouteTableId())
                .build();

        CfnSubnetRouteTableAssociation.Builder.create(this, "public-route-table-association-105")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(publicSubnet.getAttrSubnetId())
                .build();
    }

    private void createAndAttachRouteTableToPrivateSubnet(VpcStack101 vpcStack101, CfnSubnet privateSubnet) {
        CfnRouteTable routeTable = CfnRouteTable.Builder.create(this, "private-route-table-105")
                .tags(createCommonTags("private-route-table-105"))
                .vpcId(vpcStack101.getVpc1().getAttrVpcId())
                .build();

        CfnSubnetRouteTableAssociation.Builder.create(this, "private-route-table-association-105")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(privateSubnet.getAttrSubnetId())
                .build();
    }

    private CfnInternetGateway createAndAttachInternetGateway(VpcStack101 vpcStack101) {
        CfnInternetGateway internetGateway = CfnInternetGateway.Builder.create(this, "net-105-igw")
                .tags(createCommonTags("net-105-igw")).build();

        CfnVPCGatewayAttachment.Builder.create(this, "net-105-igw-vpc-attachment")
                .vpcId(vpcStack101.getVpc1().getAttrVpcId())
                .internetGatewayId(internetGateway.getAttrInternetGatewayId())
                .build();

        return internetGateway;
    }

    private void createBastionEC2(CfnSubnet subnet, CfnSecurityGroup securityGroup) {
        IMachineImage latestAMI = MachineImage.fromSsmParameter("/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2", null);
        CfnInstance.Builder.create(this, "net-105-ec2-1")
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
                .tags(createCommonTags("net-105-ec2-1"))
                .build();
    }

    private void createPrivateEC2(CfnSubnet subnet, CfnSecurityGroup securityGroup) {
        IMachineImage latestAMI = MachineImage.fromSsmParameter("/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2", null);
        CfnInstance.Builder.create(this, "net-105-ec2-2")
                .imageId(latestAMI.getImage(this).getImageId())
                .keyName("aws-workout-key")
                .instanceType("t2.micro")
                .networkInterfaces(
                        Collections.singletonList(
                                CfnInstance.NetworkInterfaceProperty.builder()
                                        .subnetId(subnet.getAttrSubnetId())
                                        .associatePublicIpAddress(false)
                                        .groupSet(Collections.singletonList(securityGroup.getAttrGroupId()))
                                        .deviceIndex("0").build()

                        ))
                .tags(createCommonTags("net-105-ec2-2"))
                .build();
    }
}

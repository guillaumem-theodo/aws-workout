package gmi.workouts.networking.workout105;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import gmi.workouts.utils.network.MyIpHelper;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;

import static gmi.workouts.utils.TagsHelper.createCommonTags;
import static gmi.workouts.utils.compute.EC2Helper.Ip.WITHOUT_PUBLIC_IP;
import static gmi.workouts.utils.compute.EC2Helper.Ip.WITH_PUBLIC_IP;
import static gmi.workouts.utils.compute.EC2Helper.createEC2;
import static gmi.workouts.utils.iam.IAMHelpers.createEC2InstanceProfile;

public class BastionStack extends Stack {

    private final CfnRouteTable privateRouteTable;

    public BastionStack(final Construct scope, final String id, final StackProps props,
                        final VpcStack vpcStack,
                        final BasicSubnetsStack subnetsStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(subnetsStack);


        CfnVPC vpc = vpcStack.getVpc();
        CfnSubnet privateSubnet = subnetsStack.getSubnet2();
        CfnSubnet publicSubnet = subnetsStack.getSubnet1();

        CfnInternetGateway igw = createAndAttachInternetGateway(vpc);

        createAndAttachRouteTableToPublicSubnet(vpc, publicSubnet, igw);
        privateRouteTable = createAndAttachRouteTableToPrivateSubnet(vpc, privateSubnet);

        CfnSecurityGroup publicSecurityGroup = createSecurityGroup(vpc);
        CfnSecurityGroup privateSecurityGroup = createPrivateSecurityGroup(vpc, publicSecurityGroup);

        CfnInstanceProfile instanceProfile = createEC2InstanceProfile(this, "net-105-ec2");
        createBastionEC2(publicSubnet, publicSecurityGroup, instanceProfile);
        createPrivateEC2(privateSubnet, privateSecurityGroup, instanceProfile);

    }
    private CfnInternetGateway createAndAttachInternetGateway(CfnVPC vpc) {
        CfnInternetGateway igw = CfnInternetGateway.Builder.create(this, "net-105-igw")
                .tags(createCommonTags("net-105-igw"))
                .build();

        CfnVPCGatewayAttachment.Builder.create(this, "net-105-igw-vpc-attachment")
                .vpcId(vpc.getAttrVpcId())
                .internetGatewayId(igw.getAttrInternetGatewayId())
                .build();

        return igw;
    }

    private void createAndAttachRouteTableToPublicSubnet(CfnVPC vpc, CfnSubnet publicSubnet, CfnInternetGateway igw) {
        CfnRouteTable routeTable = CfnRouteTable.Builder.create(this, "net-105-rt-1")
                .tags(createCommonTags("net-105-rt-1"))
                .vpcId(vpc.getAttrVpcId())
                .build();

        CfnRoute.Builder.create(this, "net-105-rt-1-internet")
                .destinationCidrBlock("0.0.0.0/0")
                .gatewayId(igw.getAttrInternetGatewayId())
                .routeTableId(routeTable.getAttrRouteTableId())
                .build();

        CfnSubnetRouteTableAssociation.Builder.create(this, "net-105-rt-1-association-subnet")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(publicSubnet.getAttrSubnetId())
                .build();
    }

    private CfnRouteTable createAndAttachRouteTableToPrivateSubnet(CfnVPC vpc, CfnSubnet privateSubnet) {
        CfnRouteTable routeTable = CfnRouteTable.Builder.create(this, "net-105-rt-2")
                .tags(createCommonTags("net-105-rt-2"))
                .vpcId(vpc.getAttrVpcId())
                .build();

        CfnSubnetRouteTableAssociation.Builder.create(this, "net-105-rt-2-association-subnet")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(privateSubnet.getAttrSubnetId())
                .build();

        return routeTable;
    }

    private CfnSecurityGroup createSecurityGroup(CfnVPC vpc) {
        String myIPAddressCIDR = MyIpHelper.getMyIPAddressCIDR();

        return CfnSecurityGroup.Builder.create(this, "net-105-sg-1")
                .vpcId(vpc.getAttrVpcId())
                .groupName("net-105-sg-1")
                .groupDescription("Security Group with ingress for PING and SSH, and egress for all")
                .securityGroupEgress(Collections.singletonList(CfnSecurityGroup.EgressProperty.builder()
                        .cidrIp("0.0.0.0/0")
                        .ipProtocol("-1").fromPort(0).toPort(0)
                        .build()
                ))
                .securityGroupIngress(Arrays.asList(
                        CfnSecurityGroup.IngressProperty.builder()
                                .cidrIp(myIPAddressCIDR)
                                .ipProtocol("tcp").fromPort(22).toPort(22)
                                .build(),
                        CfnSecurityGroup.IngressProperty.builder()
                                .cidrIp(myIPAddressCIDR)
                                .ipProtocol("icmp").fromPort(-1).toPort(-1)
                                .build()
                ))
                .tags(createCommonTags("net-105-sg-1")).build();
    }

    private CfnSecurityGroup createPrivateSecurityGroup(CfnVPC vpc, CfnSecurityGroup publicSecurityGroup) {
        return CfnSecurityGroup.Builder.create(this, "net-105-sg-2")
                .vpcId(vpc.getAttrVpcId())
                .groupName("net-105-sg-2")
                .groupDescription("Security Group with ingress for all protocols FROM other security group ONLY, egress ALL")
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
                .tags(createCommonTags("net-105-sg-2")).build();
    }

    private void createBastionEC2(CfnSubnet subnet, CfnSecurityGroup securityGroup, CfnInstanceProfile instanceProfile) {
        createEC2(this, "net-105-ec2-1", subnet, securityGroup, WITH_PUBLIC_IP,
                builder -> builder.iamInstanceProfile(instanceProfile.getInstanceProfileName()));
    }

    private void createPrivateEC2(CfnSubnet subnet, CfnSecurityGroup securityGroup, CfnInstanceProfile instanceProfile) {
        createEC2(this, "net-105-ec2-2", subnet, securityGroup, WITHOUT_PUBLIC_IP,
                        builder -> builder.iamInstanceProfile(instanceProfile.getInstanceProfileName()));
    }

    public CfnRouteTable getPrivateRouteTable() {
        return privateRouteTable;
    }


}

package gmi.workouts.networking.workout105;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.utils.IpChecker;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;

import static gmi.workouts.common.CommonIAM.createCommonEC2InstanceProfile;
import static gmi.workouts.utils.EC2Helper.createEC2;
import static gmi.workouts.utils.TagsHelper.createCommonTags;

/*
######################################################################################
## Create a BASTION architecture
## 1) create an internet gateway (IGW) for public access to/from internet (for the public subnet)
## 2) create a route table and a route to 0.0.0.0 via IGW (associated to the public subnet)
## 3) authorize PING and SSH in a security group (for the public subnet) FROM your IP address only
## 4) associate the security group to the BASTION EC2 instance
## 5) create a route table from bastion subnet to private subnet (local vpc)
## 6) authorize all traffic from bastion subnet (only) TO private subnet (within a security group)
## 7) associate the security group to the PRIVATE EC2 instances
######################################################################################

 */
public class BastionStack105 extends Stack {

    private CfnRouteTable routeTable;

    public BastionStack105(final Construct scope, final String id, final StackProps props,
                           VpcStack101 vpcStack101,
                           BasicSubnetsStack102 subnetsStack102) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(subnetsStack102);


        CfnVPC vpc = vpcStack101.getVpc();
        CfnSubnet privateSubnet = subnetsStack102.getSubnet2();
        CfnSubnet publicSubnet = subnetsStack102.getSubnet1();

        CfnInternetGateway igw = createAndAttachInternetGateway(vpc);

        createAndAttachRouteTableToPublicSubnet(vpc, publicSubnet, igw);
        createAndAttachRouteTableToPrivateSubnet(vpc, privateSubnet);

        CfnSecurityGroup publicSecurityGroup = createSecurityGroup(vpc);
        CfnSecurityGroup privateSecurityGroup = createPrivateSecurityGroup(vpc, publicSecurityGroup);

        CfnInstanceProfile commonEC2InstanceProfile = createCommonEC2InstanceProfile(this);
        createBastionEC2(publicSubnet, publicSecurityGroup, commonEC2InstanceProfile);
        createPrivateEC2(privateSubnet, privateSecurityGroup, commonEC2InstanceProfile);

    }
    private CfnInternetGateway createAndAttachInternetGateway(CfnVPC vpc) {
        CfnInternetGateway internetGateway = CfnInternetGateway.Builder.create(this, "net-105-igw")
                .tags(createCommonTags("net-105-igw"))
                .build();

        CfnVPCGatewayAttachment.Builder.create(this, "net-105-igw-vpc-attachment")
                .vpcId(vpc.getAttrVpcId())
                .internetGatewayId(internetGateway.getAttrInternetGatewayId())
                .build();

        return internetGateway;
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

        CfnSubnetRouteTableAssociation.Builder.create(this, "net-105-rt-1-association-subnet1")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(publicSubnet.getAttrSubnetId())
                .build();
    }

    private void createAndAttachRouteTableToPrivateSubnet(CfnVPC vpc, CfnSubnet privateSubnet) {
        routeTable = CfnRouteTable.Builder.create(this, "net-105-rt-2")
                .tags(createCommonTags("net-105-rt-2"))
                .vpcId(vpc.getAttrVpcId())
                .build();

        CfnSubnetRouteTableAssociation.Builder.create(this, "net-105-rt-2-association-subnet")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(privateSubnet.getAttrSubnetId())
                .build();
    }

    @NotNull
    private CfnSecurityGroup createSecurityGroup(CfnVPC vpc) {
        String myIPAddressCIDR = IpChecker.getMyIPAddressCIDR();

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

    @NotNull
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
        createEC2(this, subnet, securityGroup, "net-105-ec2-1", true, instanceProfile);
    }

    private void createPrivateEC2(CfnSubnet subnet, CfnSecurityGroup securityGroup, CfnInstanceProfile instanceProfile) {
        createEC2(this, subnet, securityGroup, "net-105-ec2-2", false, instanceProfile);
    }

    public CfnRouteTable getPrivateRouteTable() {
        return routeTable;
    }


}

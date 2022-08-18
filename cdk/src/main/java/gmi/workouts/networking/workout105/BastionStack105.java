package gmi.workouts.networking.workout105;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;

import static gmi.workouts.common.CommonIAM.createCommonEC2InstanceProfile;
import static gmi.workouts.networking.workout103.DefaultRouteAndSecurityGroupStack103.LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2;
import static gmi.workouts.utils.TagsHelper.createCommonTags;

/*
######################################################################################
## Create a BASTION architecture
## 1) create an internet gateway (IGW) for public access to/from internet (for the public subnet)
## 2) create a route table and a route to 0.0.0.0 via IGW (associated to the public subnet)
## 3) authorize PING and SSH in a security group (for the public subnet)
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
        return CfnSecurityGroup.Builder.create(this, "net-105-sg-1")
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
                .groupName("net-105-sg-1")
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
                .tags(createCommonTags("net-105-sg-1")).build();
    }

    @NotNull
    private CfnSecurityGroup createPrivateSecurityGroup(VpcStack101 vpcStack101, CfnSecurityGroup publicSecurityGroup) {
        return CfnSecurityGroup.Builder.create(this, "net-105-sg-2")
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
                .groupName("net-105-sg-2")
                .groupDescription("Security Group with ingress for all protocol FROM other security group ONLY, egress ALL")
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
    private void createAndAttachRouteTableToPublicSubnet(VpcStack101 vpcStack101, CfnSubnet publicSubnet, CfnInternetGateway igw) {
        CfnRouteTable routeTable = CfnRouteTable.Builder.create(this, "net-105-rt-1")
                .tags(createCommonTags("net-105-rt-1"))
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
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

    private void createAndAttachRouteTableToPrivateSubnet(VpcStack101 vpcStack101, CfnSubnet privateSubnet) {
        routeTable = CfnRouteTable.Builder.create(this, "net-105-rt-2")
                .tags(createCommonTags("net-105-rt-2"))
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
                .build();

        CfnSubnetRouteTableAssociation.Builder.create(this, "net-105-rt-1-association-subnet2")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(privateSubnet.getAttrSubnetId())
                .build();
    }

    //    ## Internet Gateway is a BIDIRECTIONAL gateway to Internet from VPC
    private CfnInternetGateway createAndAttachInternetGateway(VpcStack101 vpcStack101) {
        CfnInternetGateway internetGateway = CfnInternetGateway.Builder.create(this, "net-105-igw")
                .tags(createCommonTags("net-105-igw")).build();

        CfnVPCGatewayAttachment.Builder.create(this, "net-105-igw-vpc-attachment")
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
                .internetGatewayId(internetGateway.getAttrInternetGatewayId())
                .build();

        return internetGateway;
    }

    private void createBastionEC2(CfnSubnet subnet, CfnSecurityGroup securityGroup) {
        IMachineImage latestAMI = MachineImage.fromSsmParameter(LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2, null);
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
        IMachineImage latestAMI = MachineImage.fromSsmParameter(LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2, null);
        CfnInstanceProfile commonEC2InstanceProfile = createCommonEC2InstanceProfile(this);
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
                .iamInstanceProfile(commonEC2InstanceProfile.getInstanceProfileName())
                .tags(createCommonTags("net-105-ec2-2"))
                .build();
    }

    public CfnRouteTable getPrivateRouteTable() {
        return routeTable;
    }
}

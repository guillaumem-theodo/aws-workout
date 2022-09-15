package gmi.workouts.networking.workout104;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;

import static gmi.workouts.utils.TagsHelper.createCommonTags;
import static gmi.workouts.utils.compute.EC2Helper.LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2;

public class InternetAccessStack extends Stack {

    public InternetAccessStack(final Construct scope, final String id, final StackProps props,
                               final VpcStack vpcStack,
                               final BasicSubnetsStack subnetsStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(subnetsStack);

        CfnVPC vpc = vpcStack.getVpc();
        CfnSubnet subnet = subnetsStack.getSubnet2();

        CfnInternetGateway igw = createAndAttachInternetGateway(vpc);

        createAndAttachRouteTableToSubnet(vpc, subnet, igw);

        CfnSecurityGroup securityGroup = createSecurityGroup(vpc);

        createEC2(subnet, securityGroup);

    }
    private CfnInternetGateway createAndAttachInternetGateway(CfnVPC cfnVPC) {
        CfnInternetGateway internetGateway = CfnInternetGateway.Builder.create(this, "net-104-igw")
                .tags(createCommonTags("net-104-igw"))
                .build();

        CfnVPCGatewayAttachment.Builder.create(this, "net-104-igw-vpc-attachment")
                .vpcId(cfnVPC.getAttrVpcId())
                .internetGatewayId(internetGateway.getAttrInternetGatewayId())
                .build();

        return internetGateway;
    }

    private void createAndAttachRouteTableToSubnet(CfnVPC vpc, CfnSubnet subnet, CfnInternetGateway igw) {
        CfnRouteTable routeTable = CfnRouteTable.Builder.create(this, "net-104-rt")
                .vpcId(vpc.getAttrVpcId())
                .tags(createCommonTags("net-104-rt"))
                .build();

        CfnRoute.Builder.create(this, "net-104-rt-route-to-internet")
                .destinationCidrBlock("0.0.0.0/0")
                .gatewayId(igw.getAttrInternetGatewayId())
                .routeTableId(routeTable.getAttrRouteTableId())
                .build();

        CfnSubnetRouteTableAssociation.Builder.create(this, "net-104-rt-association-subnet")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(subnet.getAttrSubnetId())
                .build();
    }
    private CfnSecurityGroup createSecurityGroup(CfnVPC vpc) {
        return CfnSecurityGroup.Builder.create(this, "net-104-sg")
                .vpcId(vpc.getAttrVpcId())
                .groupName("net-104-sg")
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
                .tags(createCommonTags("net-104-sg")).build();
    }

    private void createEC2(CfnSubnet subnet, CfnSecurityGroup securityGroup) {
        IMachineImage latestAMI = MachineImage.fromSsmParameter(LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2, null);
        CfnInstance.Builder.create(this, "net-104-ec2-1")
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
                .tags(createCommonTags("net-104-ec2-1"))
                .build();
    }
}

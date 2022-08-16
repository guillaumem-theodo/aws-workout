package gmi.workouts.networking.workout104;

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

/*
######################################################################################
## Create an access FROM and TO internet on our EC2 (public EC2)
## 1) create an internet gateway (IGW)
## 2) create a route table and a route to 0.0.0.0 via IGW
## 3) authorize PING and SSH in a security group
## 4) associate the security group to the EC2 instances
## Internet Gateway is a BIDIRECTIONAL gateway to Internet from VPC
######################################################################################
 */
public class InternetAccessStack104 extends Stack {

    private static final String LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2 = "/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2";

    public InternetAccessStack104(final Construct scope, final String id, final StackProps props,
                                  VpcStack101 vpcStack101,
                                  BasicSubnetsStack102 subnetsStack102) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(subnetsStack102);

        CfnSubnet subnet2 = subnetsStack102.getSubnet2();

        CfnInternetGateway igw = createAndAttachInternetGateway(vpcStack101);

        createAndAttachRouteTableToSubnet(vpcStack101, subnet2, igw);

        CfnSecurityGroup securityGroup = createSecurityGroup(vpcStack101);

        createEC2(subnet2, securityGroup);

    }

    @NotNull
    private CfnSecurityGroup createSecurityGroup(VpcStack101 vpcStack101) {
        return CfnSecurityGroup.Builder.create(this, "net-sg-104")
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
                .groupName("net-sg-104")
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
                .tags(createCommonTags("net-sg-104")).build();
    }

    /*  ## Create a ROUTE TABLE associated to the VPC
        ## The route table routes all traffic from/to internet through Internet gateway
        ## The route table is associated to the subnet
*/
    private void createAndAttachRouteTableToSubnet(VpcStack101 vpcStack101, CfnSubnet subnet2, CfnInternetGateway igw) {
        CfnRouteTable routeTable = CfnRouteTable.Builder.create(this, "route-table-104")
                .tags(createCommonTags("route-table-104"))
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
                .build();

        CfnRoute.Builder.create(this, "route-104")
                .destinationCidrBlock("0.0.0.0/0")
                .gatewayId(igw.getAttrInternetGatewayId())
                .routeTableId(routeTable.getAttrRouteTableId())
                .build();

        CfnSubnetRouteTableAssociation.Builder.create(this, "rt-association-subnet1-104")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(subnet2.getAttrSubnetId())
                .build();
    }

    private CfnInternetGateway createAndAttachInternetGateway(VpcStack101 vpcStack101) {
        CfnInternetGateway internetGateway = CfnInternetGateway.Builder.create(this, "net-104-igw")
                .tags(createCommonTags("net-104-igw")).build();

        CfnVPCGatewayAttachment.Builder.create(this, "net-104-igw-vpc-attachment")
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
                .internetGatewayId(internetGateway.getAttrInternetGatewayId())
                .build();

        return internetGateway;
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

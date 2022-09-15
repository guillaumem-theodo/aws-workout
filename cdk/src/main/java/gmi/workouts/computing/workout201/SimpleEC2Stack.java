package gmi.workouts.computing.workout201;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import gmi.workouts.utils.compute.EC2Helper;
import gmi.workouts.utils.compute.SecurityGroupHelper;
import gmi.workouts.utils.network.InternetGatewayHelper;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.util.Collections;

import static gmi.workouts.utils.TagsHelper.createCommonTags;
import static gmi.workouts.utils.compute.SecurityGroupHelper.DefaultPort.HTTP;
import static gmi.workouts.utils.network.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.network.MyIpHelper.getMyIPAddressCIDR;

public class SimpleEC2Stack extends Stack {

    public SimpleEC2Stack(final Construct scope, final String id, final StackProps props,
                          final VpcStack vpcStack,
                          final BasicSubnetsStack subnetsStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(subnetsStack);

        CfnSubnet subnet = subnetsStack.getSubnet2();

        CfnInternetGateway igw = createAndAttachInternetGateway(this, vpcStack.getVpc(), "cpu-201-igw");
        InternetGatewayHelper.createAndAttachRouteTableToSubnets(this, "cpu-201-rt-1", vpcStack.getVpc(), igw, subnet);

        CfnSecurityGroup securityGroup = createSecurityGroup(vpcStack.getVpc());

        IMachineImage latestAMI = MachineImage.fromSsmParameter(EC2Helper.LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2, null);

        CfnInstance.Builder.create(this, "cpu-201-ec2-1")
                .imageId(latestAMI.getImage(this).getImageId())
                .keyName("aws-workout-key")
                .instanceType("t2.micro")
                .networkInterfaces(  // This is the way to add a public IP to the EC2 -> create a network interface (ENI)
                        Collections.singletonList(
                                CfnInstance.NetworkInterfaceProperty.builder()
                                        .subnetId(subnet.getAttrSubnetId())
                                        .associatePublicIpAddress(true)
                                        .groupSet(Collections.singletonList(securityGroup.getAttrGroupId()))
                                        .deviceIndex("0").build()

                        ))

                .tags(createCommonTags("cpu-201-ec2-1"))
                .build();

    }

    private  CfnSecurityGroup createSecurityGroup(CfnVPC vpc) {
        CfnSecurityGroup securityGroup = SecurityGroupHelper.createSecurityGroup(this, vpc, "cpu-201-sg", HTTP);

        CfnSecurityGroupIngress.Builder.create(this, "cpu-201-sg-rule-ssh")
                .groupId(securityGroup.getAttrGroupId())
                .fromPort(22).toPort(22).ipProtocol("tcp")
                .cidrIp(getMyIPAddressCIDR())
                .build();
        CfnSecurityGroupIngress.Builder.create(this, "cpu-201-sg-rule-ping")
                .groupId(securityGroup.getAttrGroupId())
                .fromPort(-1).toPort(-1).ipProtocol("icmp")
                .cidrIp(getMyIPAddressCIDR())
                .build();
        return securityGroup;
    }

}

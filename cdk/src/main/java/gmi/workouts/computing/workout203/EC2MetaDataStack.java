package gmi.workouts.computing.workout203;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import gmi.workouts.utils.compute.EC2Helper;
import gmi.workouts.utils.compute.SecurityGroupHelper;
import gmi.workouts.utils.network.InternetGatewayHelper;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import static gmi.workouts.utils.TagsHelper.createCommonTags;
import static gmi.workouts.utils.compute.SecurityGroupHelper.DefaultPort.HTTP;
import static gmi.workouts.utils.network.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.network.MyIpHelper.getMyIPAddressCIDR;

public class EC2MetaDataStack extends Stack {

    private static final String USER_DATA_SCRIPT =
            "#!/bin/bash\n" +
                    "sudo su\n" +
                    "yum update -y\n" +
                    "yum install -y httpd\n" +
                    "systemctl start httpd\n" +
                    "systemctl enable httpd\n" +
                    "\n" +
                    "public_ip=$(curl 169.254.169.254/latest/meta-data/public-ipv4)\n" +    // THIS LINE SHOWS HOW TO GET DATA from META DATA service
                    "\n" +
                    "echo \"Hello World from $(hostname -f) - PUBLIC IP: $public_ip\" > /var/www/html/index.html\n";

    public EC2MetaDataStack(final Construct scope, final String id, final StackProps props,
                            final VpcStack vpcStack,
                            final BasicSubnetsStack subnetsStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(subnetsStack);

        CfnSubnet subnet = subnetsStack.getSubnet2();

        CfnInternetGateway igw = createAndAttachInternetGateway(this, vpcStack.getVpc(), "cpu-203-igw");
        InternetGatewayHelper.createAndAttachRouteTableToSubnets(this, "cpu-203-rt-1", vpcStack.getVpc(), igw, subnet);

        CfnSecurityGroup securityGroup = createSecurityGroup(vpcStack.getVpc());

        IMachineImage latestAMI = MachineImage.fromSsmParameter(EC2Helper.LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2, null);

        CfnInstance.Builder.create(this, "cpu-203-ec2-1")
                .imageId(latestAMI.getImage(this).getImageId())
                .keyName("aws-workout-key")
                .instanceType("t2.micro")
                .userData(Base64.getEncoder().encodeToString(USER_DATA_SCRIPT.getBytes(StandardCharsets.UTF_8)))
                .networkInterfaces(  // This is the way to add a public IP to the EC2 -> create a network interface (ENI)
                        Collections.singletonList(
                                CfnInstance.NetworkInterfaceProperty.builder()
                                        .subnetId(subnet.getAttrSubnetId())
                                        .associatePublicIpAddress(true)
                                        .groupSet(Collections.singletonList(securityGroup.getAttrGroupId()))
                                        .deviceIndex("0").build()

                        ))

                .tags(createCommonTags("cpu-203-ec2-1"))
                .build();

    }

    private  CfnSecurityGroup createSecurityGroup(CfnVPC vpc) {
        CfnSecurityGroup securityGroup = SecurityGroupHelper.createSecurityGroup(this, vpc, "cpu-203-sg", HTTP);

        CfnSecurityGroupIngress.Builder.create(this, "cpu-203-sg-rule-ssh")
                .groupId(securityGroup.getAttrGroupId())
                .fromPort(22).toPort(22).ipProtocol("tcp")
                .cidrIp(getMyIPAddressCIDR())
                .build();
        CfnSecurityGroupIngress.Builder.create(this, "cpu-203-sg-rule-ping")
                .groupId(securityGroup.getAttrGroupId())
                .fromPort(-1).toPort(-1).ipProtocol("icmp")
                .cidrIp(getMyIPAddressCIDR())
                .build();
        return securityGroup;
    }

}

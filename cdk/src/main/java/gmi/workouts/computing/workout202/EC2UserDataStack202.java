package gmi.workouts.computing.workout202;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.utils.EC2Helper;
import gmi.workouts.utils.InternetGatewayHelper;
import gmi.workouts.utils.SecurityGroupHelper;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.IpChecker.getMyIPAddressCIDR;
import static gmi.workouts.utils.SecurityGroupHelper.DefaultPort.HTTP;
import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class EC2UserDataStack202 extends Stack {

    private static final String USER_DATA_SCRIPT =
            "#!/bin/bash\n" +
            "sudo su\n" +
            "yum update -y\n" +
            "yum install -y httpd\n" +
            "systemctl start httpd\n" +
            "systemctl enable httpd\n" +
            "echo \"Hello World from $(hostname -f)\" > /var/www/html/index.html\n";

    public EC2UserDataStack202(final Construct scope, final String id, final StackProps props,
                               VpcStack101 vpcStack101,
                               BasicSubnetsStack102 basicSubnetsStack102) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(basicSubnetsStack102);

        CfnSubnet subnet2 = basicSubnetsStack102.getSubnet2();

        CfnInternetGateway internetGateway = createAndAttachInternetGateway(this, vpcStack101.getVpc(), "cpu-202-igw");
        InternetGatewayHelper.createAndAttachRouteTableToSubnets(this, "cpu-202-rt-1", vpcStack101.getVpc(), internetGateway, subnet2);

        CfnSecurityGroup securityGroup = createSecurityGroup(vpcStack101);

        IMachineImage latestAMI = MachineImage.fromSsmParameter(EC2Helper.LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2, null);

        CfnInstance.Builder.create(this, "cpu-202-ec2-1")
                .imageId(latestAMI.getImage(this).getImageId())
                .keyName("aws-workout-key")
                .instanceType("t2.micro")
                .userData(Base64.getEncoder().encodeToString(USER_DATA_SCRIPT.getBytes(StandardCharsets.UTF_8)))
                .networkInterfaces(  // This is the way to add a public IP to the EC2 -> create a network interface (ENI)
                        Collections.singletonList(
                                CfnInstance.NetworkInterfaceProperty.builder()
                                        .subnetId(subnet2.getAttrSubnetId())
                                        .associatePublicIpAddress(true)
                                        .groupSet(Collections.singletonList(securityGroup.getAttrGroupId()))
                                        .deviceIndex("0").build()

                        ))

                .tags(createCommonTags("cpu-202-ec2-1"))
                .build();

    }

    @NotNull
    private  CfnSecurityGroup createSecurityGroup(VpcStack101 vpcStack101) {
        CfnSecurityGroup securityGroup = SecurityGroupHelper.createSecurityGroup(this, vpcStack101.getVpc(), "cpu-202-sg", HTTP);

        CfnSecurityGroupIngress.Builder.create(this, "cpu-202-sg-rule-ssh")
                .groupId(securityGroup.getAttrGroupId())
                .fromPort(22).toPort(22).ipProtocol("tcp")
                .cidrIp(getMyIPAddressCIDR())
                .build();
        CfnSecurityGroupIngress.Builder.create(this, "cpu-202-sg-rule-ping")
                .groupId(securityGroup.getAttrGroupId())
                .fromPort(-1).toPort(-1).ipProtocol("icmp")
                .cidrIp(getMyIPAddressCIDR())
                .build();
        return securityGroup;
    }

}

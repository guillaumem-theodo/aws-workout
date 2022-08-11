package gmi.workouts.networking.workout103;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.CfnTag;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gmi.workouts.CdkApp.PURPOSE;

public class DefaultRouteAndSecurityGroupStack103 extends Stack {

    public DefaultRouteAndSecurityGroupStack103(final Construct scope, final String id, final StackProps props, VpcStack101 vpcStack101, BasicSubnetsStack102 networkingBasicSubnets102) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(networkingBasicSubnets102);

        CfnSubnet subnet2 = networkingBasicSubnets102.getSubnet2();

        IMachineImage latestAMI = MachineImage.fromSsmParameter("/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2", null);

        CfnInstance.Builder.create(this, "net-103-ec2-1")
                .imageId(latestAMI.getImage(this).getImageId())
                .keyName("aws-workout-key")
                .instanceType("t2.micro")
                .networkInterfaces(
                        Collections.singletonList(
                                CfnInstance.NetworkInterfaceProperty.builder()
                                        .subnetId(subnet2.getAttrSubnetId())
                                        .associatePublicIpAddress(true)
                                        .deviceIndex("0").build()

                        ))
                .tags(createCommonTags("net-103-ec2-1"))
                .build();

    }
    @NotNull
    private static List<CfnTag> createCommonTags(String value) {
        return Arrays.asList(
                CfnTag.builder().key("Purpose").value(PURPOSE).build(),
                CfnTag.builder().key("Name").value(value).build()
        );
    }
}

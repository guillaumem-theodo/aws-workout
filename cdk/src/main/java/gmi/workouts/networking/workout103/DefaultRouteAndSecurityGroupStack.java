package gmi.workouts.networking.workout103;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import gmi.workouts.utils.compute.EC2Helper;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnInstance;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.amazon.awscdk.services.ec2.IMachineImage;
import software.amazon.awscdk.services.ec2.MachineImage;
import software.constructs.Construct;

import java.util.Collections;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class DefaultRouteAndSecurityGroupStack extends Stack {

    public DefaultRouteAndSecurityGroupStack(final Construct scope, final String id, final StackProps props,
                                             final VpcStack vpcStack,
                                             final BasicSubnetsStack basicSubnetsStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(basicSubnetsStack);

        CfnSubnet subnet2 = basicSubnetsStack.getSubnet2();

        IMachineImage latestAMI = MachineImage.fromSsmParameter(EC2Helper.LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2, null);

        CfnInstance.Builder.create(this, "net-103-ec2-1")
                .imageId(latestAMI.getImage(this).getImageId())
                .keyName("aws-workout-key")
                .instanceType("t2.micro")
                .networkInterfaces(  // This is the way to add a public IP to the EC2 -> create a network interface (ENI)
                        Collections.singletonList(
                                CfnInstance.NetworkInterfaceProperty.builder()
                                        .subnetId(subnet2.getAttrSubnetId())
                                        .associatePublicIpAddress(true)
                                        .deviceIndex("0").build()

                        ))
                .tags(createCommonTags("net-103-ec2-1"))
                .build();

    }

}

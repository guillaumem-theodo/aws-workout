package gmi.workouts.utils;

import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.constructs.Construct;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class EC2Helper {
    public static final String LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2 = "/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2";
    public static final String AWS_WORKOUT_KEY = "aws-workout-key";
    public static final String INSTANCE_TYPE = "t2.micro";

    public static CfnInstance createEC2(Construct scope,
                                        String name,
                                        CfnSubnet subnet, CfnSecurityGroup securityGroup,
                                        boolean withPublicIP,
                                        CfnInstanceProfile instanceProfile,
                                        String userDataScript) {
        IMachineImage latestAMI = MachineImage.fromSsmParameter(LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2, null);

        CfnInstance.Builder builder = CfnInstance.Builder.create(scope, name)
                .imageId(latestAMI.getImage(scope).getImageId())
                .keyName(AWS_WORKOUT_KEY)
                .instanceType(INSTANCE_TYPE)
                .networkInterfaces(
                        Collections.singletonList(
                                CfnInstance.NetworkInterfaceProperty.builder()
                                        .subnetId(subnet.getAttrSubnetId())
                                        .associatePublicIpAddress(withPublicIP)
                                        .groupSet(Collections.singletonList(securityGroup.getAttrGroupId()))
                                        .deviceIndex("0").build()

                        ))
                .tags(createCommonTags(name));

        if(instanceProfile != null){
            builder.iamInstanceProfile(instanceProfile.getInstanceProfileName());
        }
        if(userDataScript != null){
            builder.userData(Base64.getEncoder().encodeToString(userDataScript.getBytes(StandardCharsets.UTF_8)));
        }
        return builder.build();
    }

}

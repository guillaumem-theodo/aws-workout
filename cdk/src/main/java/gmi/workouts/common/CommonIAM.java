package gmi.workouts.common;

import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.amazon.awscdk.services.iam.CfnRole;
import software.constructs.Construct;

import java.util.Collections;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class CommonIAM {
    public static CfnInstanceProfile createCommonEC2InstanceProfile(final Construct scope){

        CfnRole cfnRole = CfnRole.Builder.create(scope, "common-role-for-ec2")
                .assumeRolePolicyDocument("{" +
                        "  \"Version\": \"2012-10-17\"," +
                        "  \"Statement\": [" +
                        "    {" +
                        "      \"Action\": \"sts:AssumeRole\"," +
                        "      \"Principal\": {" +
                        "        \"Service\": \"ec2.amazonaws.com\"" +
                        "      }," +
                        "      \"Effect\": \"Allow\"," +
                        "      \"Sid\": \"\"" +
                        "    }" +
                        "  ]" +
                        "}")
                .managedPolicyArns(Collections.singletonList("arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"))
                .tags(createCommonTags("common-iam-ec2-role"))
                .build();

        return CfnInstanceProfile.Builder.create(scope, "common-ec2-instance-profile")
                .roles(Collections.singletonList(cfnRole.getAttrRoleId()))
                .build();
    }
}

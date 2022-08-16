package gmi.workouts.common;

import software.amazon.awscdk.services.iam.*;
import software.constructs.Construct;

import java.util.Collections;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class CommonIAM {
    public static CfnInstanceProfile createCommonEC2InstanceProfile(final Construct scope) {


        Role role = Role.Builder.create(scope, "common-iam-ec2-role")
                .assumedBy(ServicePrincipal.Builder.create("ec2.amazonaws.com").build())
                .managedPolicies(Collections.singletonList(
                        ManagedPolicy.fromManagedPolicyArn(scope, "common-iam-managed-policy", "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess")))
                .roleName("common-iam-ec2-role")
                .build();


        return CfnInstanceProfile.Builder.create(scope, "common-ec2-instance-profile")
                .roles(Collections.singletonList(role.getRoleName()))
                .instanceProfileName("common-ec2-instance-profile")
                .build();
    }
}

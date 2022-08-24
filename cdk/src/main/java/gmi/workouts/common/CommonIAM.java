package gmi.workouts.common;

import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.constructs.Construct;

import java.util.Collections;

public class CommonIAM {

    private static final String READ_ONLY_S3_ACCESS = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess";

    public static CfnInstanceProfile createCommonEC2InstanceProfile(Construct scope) {

        Role role = Role.Builder.create(scope, "common-iam-ec2-role")
                .assumedBy(ServicePrincipal.Builder.create("ec2.amazonaws.com").build())
                .managedPolicies(Collections.singletonList(
                        ManagedPolicy.fromManagedPolicyArn(scope, "common-iam-managed-policy", READ_ONLY_S3_ACCESS)))
                .roleName("common-iam-ec2-role")
                .build();


        return CfnInstanceProfile.Builder.create(scope, "common-ec2-instance-profile")
                .roles(Collections.singletonList(role.getRoleName()))
                .instanceProfileName("common-ec2-instance-profile")
                .build();
    }
}

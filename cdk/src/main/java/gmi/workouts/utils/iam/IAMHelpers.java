package gmi.workouts.utils.iam;

import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.constructs.Construct;

import java.util.Collections;

public class IAMHelpers {

    public static final String READ_ONLY_S3_ACCESS = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess";
    public static final String LAMBDA_EXECUTION = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole";

    public static CfnInstanceProfile createEC2InstanceProfile(Construct scope, String id) {

        Role role = Role.Builder.create(scope, id + "-role")
                .assumedBy(ServicePrincipal.Builder.create("ec2.amazonaws.com").build())
                .managedPolicies(Collections.singletonList(
                        ManagedPolicy.fromManagedPolicyArn(scope, id+ "-managed-policy", READ_ONLY_S3_ACCESS)))
                .roleName(id + "-role")
                .build();

        return CfnInstanceProfile.Builder.create(scope, id+"-instance-profile")
                .roles(Collections.singletonList(role.getRoleName()))
                .instanceProfileName(id+"-instance-profile")
                .build();
    }
}

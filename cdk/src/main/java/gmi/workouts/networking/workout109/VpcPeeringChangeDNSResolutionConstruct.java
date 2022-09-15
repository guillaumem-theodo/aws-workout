package gmi.workouts.networking.workout109;

import software.amazon.awscdk.customresources.AwsCustomResource;
import software.amazon.awscdk.customresources.AwsCustomResourcePolicy;
import software.amazon.awscdk.customresources.AwsSdkCall;
import software.amazon.awscdk.customresources.PhysicalResourceId;
import software.amazon.awscdk.services.ec2.CfnVPCPeeringConnection;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;

import java.util.Collections;
import java.util.Map;

public class VpcPeeringChangeDNSResolutionConstruct extends Construct {
    public VpcPeeringChangeDNSResolutionConstruct(final Construct scope, final String id, final CfnVPCPeeringConnection peeringConnection) {
        super(scope, id);


        AwsSdkCall onCreate = AwsSdkCall.builder().action("modifyVpcPeeringConnectionOptions").service("EC2")
                .physicalResourceId(PhysicalResourceId.of(peeringConnection.getRef()))
                .parameters(Map.of(
                        "VpcPeeringConnectionId", peeringConnection.getRef(),
                        "AccepterPeeringConnectionOptions", Map.of("AllowDnsResolutionFromRemoteVpc", true),
                        "RequesterPeeringConnectionOptions", Map.of("AllowDnsResolutionFromRemoteVpc", true)
                        ))
                .build();
        AwsSdkCall onDelete = AwsSdkCall.builder().action("modifyVpcPeeringConnectionOptions").service("EC2")
                .parameters(Map.of(
                        "VpcPeeringConnectionId", peeringConnection.getRef(),
                        "AccepterPeeringConnectionOptions", Map.of("AllowDnsResolutionFromRemoteVpc", false),
                        "RequesterPeeringConnectionOptions", Map.of("AllowDnsResolutionFromRemoteVpc", false)
                        ))
                .build();

        AwsCustomResource customResource = AwsCustomResource.Builder.create(this, "allow-peering-dns-resolution")
                .onCreate(onCreate)
                .onUpdate(onCreate)
                .onDelete(onDelete)
                .policy(AwsCustomResourcePolicy.fromStatements(
                        Collections.singletonList(
                                PolicyStatement.Builder.create()
                                        .actions(Collections.singletonList("ec2:ModifyVpcPeeringConnectionOptions"))
                                        .effect(Effect.ALLOW)
                                        .resources(Collections.singletonList("*"))
                                        .build())))
                .logRetention(RetentionDays.ONE_DAY)
                .build();

        customResource.getNode().addDependency(peeringConnection);
    }
}

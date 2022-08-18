package gmi.workouts;

import gmi.workouts.common.S3ForTestsStack;
import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.networking.workout103.DefaultRouteAndSecurityGroupStack103;
import gmi.workouts.networking.workout104.InternetAccessStack104;
import gmi.workouts.networking.workout105.BastionStack105;
import gmi.workouts.networking.workout106.NatGatewayStack106;
import gmi.workouts.networking.workout107.VpcEndpointStack107;
import gmi.workouts.networking.workout107nat.VpcEndpointWithNatStack107;
import gmi.workouts.networking.workout108.DnsStack108;
import gmi.workouts.networking.workout109.VpcPeeringStack109;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.cxapi.CloudAssembly;
import software.amazon.awscdk.cxapi.CloudFormationStackArtifact;
import software.constructs.IConstruct;
import software.constructs.Node;

import java.util.List;

public class CdkApp {
    public static final String PURPOSE = "aws-workout";
    public static final String TUTORIAL_REGION = System.getenv("TUTORIAL_REGION");
    public static final String TUTORIAL_ANOTHER_REGION = System.getenv("TUTORIAL_ANOTHER_REGION");

    public static void main(final String[] args) {
        App app = new App();

        Environment firstEnvironment = Environment.builder()
                .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                .region(TUTORIAL_REGION)
                .build();

        Environment secondEnvironment = Environment.builder()
                .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                .region(TUTORIAL_ANOTHER_REGION)
                .build();

        addNetworkingTutorialsStacks(app, firstEnvironment, secondEnvironment);

        app.synth();

    }

    private static void addNetworkingTutorialsStacks(App app, Environment firstEnvironment, Environment secondEnvironment) {
        VpcStack101 vpcStack101 = new VpcStack101(app, "workout-101-basic-vpc",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build());

        BasicSubnetsStack102 networkingBasicSubnets102 = new BasicSubnetsStack102(app, "workout-102-basic-subnets",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build(), vpcStack101);

        DefaultRouteAndSecurityGroupStack103 networkingDefaultRouteAndSg103 =
                new DefaultRouteAndSecurityGroupStack103(app, "workout-103-vpc-default-route-default-sg",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build(), vpcStack101, networkingBasicSubnets102);

        InternetAccessStack104 internetAccessStack104 =
                new InternetAccessStack104(app, "workout-104-internet-access",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build(), vpcStack101, networkingBasicSubnets102);

        BastionStack105 bastionStack105 =
                new BastionStack105(app, "workout-105-bastion",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build(), vpcStack101, networkingBasicSubnets102);

        NatGatewayStack106 natGatewayStack106 =
                new NatGatewayStack106(app, "workout-106-nat-gtw",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build(), vpcStack101, networkingBasicSubnets102, bastionStack105);

        S3ForTestsStack s3ForTestsInFirstRegionStack = new S3ForTestsStack(app, "common-s3-region-1",
                StackProps.builder()
                                .env(firstEnvironment)
                                .build(), "s3-bucket-1");
        S3ForTestsStack s3ForTestsInSecondRegionStack = new S3ForTestsStack(app, "common-s3-region-2",
                StackProps.builder()
                                .env(secondEnvironment)
                                .build(), "s3-bucket-2");

        VpcEndpointStack107 vpcEndpointStack107 =
                new VpcEndpointStack107(app, "workout-107-vpc-endpoint",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build(),
                        vpcStack101, networkingBasicSubnets102, bastionStack105,
                        s3ForTestsInFirstRegionStack,
                        s3ForTestsInSecondRegionStack);

        VpcEndpointWithNatStack107 vpcEndpointWithNatStack107 =
                new VpcEndpointWithNatStack107(app, "workout-107-vpc-endpoint-with-nat",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build(),
                        vpcStack101, networkingBasicSubnets102, bastionStack105, natGatewayStack106,
                        s3ForTestsInFirstRegionStack,
                        s3ForTestsInSecondRegionStack);

        DnsStack108 dnsStack108 =
                new DnsStack108(app, "workout-108-dns",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build());

        VpcPeeringStack109 vpcPeeringStack109 =
                new VpcPeeringStack109(app, "workout-109-vpc-peering",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build());   }
}


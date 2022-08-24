package gmi.workouts;

import gmi.workouts.common.S3ForTestsStack;
import gmi.workouts.computing.workout201.SimpleEC2Stack201;
import gmi.workouts.computing.workout202.EC2UserDataStack202;
import gmi.workouts.computing.workout203.EC2MetaDataStack203;
import gmi.workouts.computing.workout204.EC2RoleStack204;
import gmi.workouts.computing.workout205.ALBParentStack205;
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
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Collections;

public class CdkApp {
    public static final String PURPOSE = "aws-workout";
    public static final String TUTORIAL_REGION = System.getenv("TUTORIAL_REGION");
    public static final String TUTORIAL_ANOTHER_REGION = System.getenv("TUTORIAL_ANOTHER_REGION");
    private static VpcStack101 vpcStack101;
    private static BasicSubnetsStack102 networkingBasicSubnets102;
    private static S3ForTestsStack s3ForTestsInFirstRegionStack;
    private static S3ForTestsStack s3ForTestsInSecondRegionStack;

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
        addComputingTutorialsStacks(app, firstEnvironment, secondEnvironment);

        app.synth();

    }

    private static void addComputingTutorialsStacks(App app, Environment firstEnvironment, Environment secondEnvironment) {
        SimpleEC2Stack201 simpleEC2Stack201 = new SimpleEC2Stack201(app, "workout-201-basic-ec2",
                createStackProps(firstEnvironment), vpcStack101, networkingBasicSubnets102);

        EC2UserDataStack202 ec2UserDataStack202 = new EC2UserDataStack202(app, "workout-202-user-data",
                createStackProps(firstEnvironment), vpcStack101, networkingBasicSubnets102);

        EC2MetaDataStack203 ec2MetaDataStack203 = new EC2MetaDataStack203(app, "workout-203-meta-data",
                createStackProps(firstEnvironment), vpcStack101, networkingBasicSubnets102);

        EC2RoleStack204 ec2RoleStack204 = new EC2RoleStack204(app, "workout-204-ec2-role",
                createStackProps(firstEnvironment), vpcStack101, networkingBasicSubnets102,
                s3ForTestsInFirstRegionStack, s3ForTestsInSecondRegionStack);

        ALBParentStack205 albStack205 = new ALBParentStack205(app, "workout-205-alb",
                createStackProps(firstEnvironment), vpcStack101, networkingBasicSubnets102);
    }

    private static void addNetworkingTutorialsStacks(App app, Environment firstEnvironment, Environment secondEnvironment) {
        vpcStack101 = new VpcStack101(app, "workout-101-basic-vpc",
                createStackProps(firstEnvironment));

        networkingBasicSubnets102 = new BasicSubnetsStack102(app, "workout-102-basic-subnets",
                createStackProps(firstEnvironment), vpcStack101);

        DefaultRouteAndSecurityGroupStack103 networkingDefaultRouteAndSg103 =
                new DefaultRouteAndSecurityGroupStack103(app, "workout-103-vpc-default-route-default-sg",
                        createStackProps(firstEnvironment), vpcStack101, networkingBasicSubnets102);

        InternetAccessStack104 internetAccessStack104 =
                new InternetAccessStack104(app, "workout-104-internet-access",
                        createStackProps(firstEnvironment), vpcStack101, networkingBasicSubnets102);

        BastionStack105 bastionStack105 =
                new BastionStack105(app, "workout-105-bastion",
                        createStackProps(firstEnvironment), vpcStack101, networkingBasicSubnets102);

        NatGatewayStack106 natGatewayStack106 =
                new NatGatewayStack106(app, "workout-106-nat-gtw",
                        createStackProps(firstEnvironment), vpcStack101, networkingBasicSubnets102, bastionStack105);

        s3ForTestsInFirstRegionStack = new S3ForTestsStack(app, "common-s3-region-1",
                createStackProps(firstEnvironment), "s3-bucket-1");
        s3ForTestsInSecondRegionStack = new S3ForTestsStack(app, "common-s3-region-2",
                createStackProps(secondEnvironment), "s3-bucket-2");

        VpcEndpointStack107 vpcEndpointStack107 =
                new VpcEndpointStack107(app, "workout-107-vpc-endpoint",
                        createStackProps(firstEnvironment),
                        vpcStack101, networkingBasicSubnets102, bastionStack105,
                        s3ForTestsInFirstRegionStack,
                        s3ForTestsInSecondRegionStack);

        VpcEndpointWithNatStack107 vpcEndpointWithNatStack107 =
                new VpcEndpointWithNatStack107(app, "workout-107-vpc-endpoint-with-nat",
                        createStackProps(firstEnvironment),
                        vpcStack101, networkingBasicSubnets102, bastionStack105, natGatewayStack106,
                        s3ForTestsInFirstRegionStack,
                        s3ForTestsInSecondRegionStack);

        DnsStack108 dnsStack108 =
                new DnsStack108(app, "workout-108-dns",
                        createStackProps(firstEnvironment));

        VpcPeeringStack109 vpcPeeringStack109 =
                new VpcPeeringStack109(app, "workout-109-vpc-peering",
                        createStackProps(firstEnvironment));   }

    @NotNull
    private static StackProps createStackProps(Environment environment) {
        return StackProps.builder()
                .env(environment)
                .tags(Collections.singletonMap("Purpose", PURPOSE))
                .build();
    }
}


package gmi.workouts;

import gmi.workouts.common.S3ForTestsStack;
import gmi.workouts.computing.workout201.SimpleEC2Stack;
import gmi.workouts.computing.workout202.EC2UserDataStack;
import gmi.workouts.computing.workout203.EC2MetaDataStack;
import gmi.workouts.computing.workout204.EC2RoleStack;
import gmi.workouts.computing.workout205.ALBParentStack;
import gmi.workouts.computing.workout206.ASGParentStack206;
import gmi.workouts.computing.workout207.ECSParentStack;
import gmi.workouts.computing.workout208.ParentStack;
import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import gmi.workouts.networking.workout103.DefaultRouteAndSecurityGroupStack;
import gmi.workouts.networking.workout104.InternetAccessStack;
import gmi.workouts.networking.workout105.BastionStack;
import gmi.workouts.networking.workout106.NatGatewayStack;
import gmi.workouts.networking.workout107.VpcEndpointStack;
import gmi.workouts.networking.workout107nat.VpcEndpointWithNatStack;
import gmi.workouts.networking.workout108.DnsStack;
import gmi.workouts.networking.workout109.VpcPeeringNetworkStack;
import gmi.workouts.networking.workout109.VpcPeeringStack;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Collections;

public class CdkApp {
    public static final String PURPOSE = "aws-workout";
    public static final String TUTORIAL_REGION = System.getenv("TUTORIAL_REGION");
    public static final String TUTORIAL_ANOTHER_REGION = System.getenv("TUTORIAL_ANOTHER_REGION");
    private static VpcStack vpcStack;
    private static BasicSubnetsStack networkingBasicSubnets;
    private static S3ForTestsStack s3InFirstRegionStack;
    private static S3ForTestsStack s3InSecondRegionStack;

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

        s3InFirstRegionStack = new S3ForTestsStack(app, "common-s3-region-1",
                createStackProps(firstEnvironment), "s3-bucket-1");

        s3InSecondRegionStack = new S3ForTestsStack(app, "common-s3-region-2",
                createStackProps(secondEnvironment), "s3-bucket-2");

        StackProps stackProps = createStackProps(firstEnvironment);
        addNetworkingTutorialsStacks(app, stackProps);
        addComputingTutorialsStacks(app, stackProps);

        app.synth();

    }

    private static void addComputingTutorialsStacks(App app, StackProps stackProps) {

        SimpleEC2Stack simpleEC2Stack = new SimpleEC2Stack(app, "workout-201-basic-ec2",
                stackProps, vpcStack, networkingBasicSubnets);

        EC2UserDataStack ec2UserDataStack = new EC2UserDataStack(app, "workout-202-user-data",
                stackProps, vpcStack, networkingBasicSubnets);

        EC2MetaDataStack ec2MetaDataStack = new EC2MetaDataStack(app, "workout-203-meta-data",
                stackProps, vpcStack, networkingBasicSubnets);

        EC2RoleStack ec2RoleStack = new EC2RoleStack(app, "workout-204-ec2-role",
                stackProps, vpcStack, networkingBasicSubnets,
                s3InFirstRegionStack, s3InSecondRegionStack);

        ALBParentStack albStack = new ALBParentStack(app, "workout-205-alb",
                stackProps, vpcStack, networkingBasicSubnets);

        ASGParentStack206 asgParentStack = new ASGParentStack206(app, "workout-206-auto-scaling",
                stackProps, vpcStack, networkingBasicSubnets);

        ECSParentStack ecsParentStack = new ECSParentStack(app, "workout-207-simple-ECS",
                stackProps, vpcStack, networkingBasicSubnets);

        ParentStack lambdaStack = new ParentStack(app, "workout-208-sls-lambda",
                stackProps,
                vpcStack, networkingBasicSubnets, s3InFirstRegionStack);
    }

    private static void addNetworkingTutorialsStacks(App app, StackProps stackProps) {
        vpcStack = new VpcStack(app, "workout-101-basic-vpc", stackProps);

        networkingBasicSubnets = new BasicSubnetsStack(app, "workout-102-basic-subnets", stackProps, vpcStack);

        DefaultRouteAndSecurityGroupStack networkingDefaultRouteAndSg =
                new DefaultRouteAndSecurityGroupStack(app, "workout-103-vpc-default-route-default-sg",
                        stackProps, vpcStack, networkingBasicSubnets);

        InternetAccessStack internetAccessStack = new InternetAccessStack(app, "workout-104-internet-access",
                stackProps, vpcStack, networkingBasicSubnets);

        BastionStack bastionStack = new BastionStack(app, "workout-105-bastion",
                stackProps, vpcStack, networkingBasicSubnets);

        NatGatewayStack natGatewayStack = new NatGatewayStack(app, "workout-106-nat-gtw",
                stackProps, vpcStack, networkingBasicSubnets, bastionStack);


        VpcEndpointStack vpcEndpointStack =
                new VpcEndpointStack(app, "workout-107-vpc-endpoint",
                        stackProps,
                        vpcStack, networkingBasicSubnets, bastionStack,
                        s3InFirstRegionStack,
                        s3InSecondRegionStack);

        VpcEndpointWithNatStack vpcEndpointWithNatStack =
                new VpcEndpointWithNatStack(app, "workout-107-vpc-endpoint-with-nat",
                        stackProps,
                        vpcStack, networkingBasicSubnets, bastionStack, natGatewayStack,
                        s3InFirstRegionStack,
                        s3InSecondRegionStack);

        DnsStack dnsStack = new DnsStack(app, "workout-108-dns", stackProps);

        VpcPeeringNetworkStack peeringNetworkStack = new VpcPeeringNetworkStack(app, "stack-109-vpc-peering-network",
                stackProps);

        VpcPeeringStack vpcPeeringStack = new VpcPeeringStack(app, "workout-109-vpc-peering",
                stackProps, peeringNetworkStack);
    }

    private static StackProps createStackProps(Environment environment) {
        return StackProps.builder()
                .env(environment)
                .tags(Collections.singletonMap("Purpose", PURPOSE))
                .build();
    }
}


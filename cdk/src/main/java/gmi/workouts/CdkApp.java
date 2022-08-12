package gmi.workouts;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.networking.workout103.DefaultRouteAndSecurityGroupStack103;
import gmi.workouts.networking.workout104.InternetAccessStack104;
import gmi.workouts.networking.workout105.BastionStack105;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

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

        addNetworkingTutorialsStacks(app, firstEnvironment);

        app.synth();
    }

    private static void addNetworkingTutorialsStacks(App app, Environment firstEnvironment) {
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
    }
}


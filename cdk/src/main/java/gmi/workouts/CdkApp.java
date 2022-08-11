package gmi.workouts;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.networking.workout103.DefaultRouteAndSecurityGroupStack103;
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
        VpcStack101 vpcStack101 = new VpcStack101(app, "NetworkingVpc101",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build());

        BasicSubnetsStack102 networkingBasicSubnets102 = new BasicSubnetsStack102(app, "NetworkingBasicSubnets102",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build(), vpcStack101);

        DefaultRouteAndSecurityGroupStack103 networkingDefaultRouteAndSg103 =
                new DefaultRouteAndSecurityGroupStack103(app, "NetworkingDefaultRouteAndSg103",
                StackProps.builder()
                        .env(firstEnvironment)
                        .build(), vpcStack101, networkingBasicSubnets102);    }
}

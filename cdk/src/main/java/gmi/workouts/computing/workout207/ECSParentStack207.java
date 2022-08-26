package gmi.workouts.computing.workout207;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class ECSParentStack207 extends Stack {

    public ECSParentStack207(final Construct scope, final String id, final StackProps props,
                             VpcStack101 vpcStack101,
                             BasicSubnetsStack102 basicSubnetsStack102) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(basicSubnetsStack102);

        ECSNetworkStack207 ecsNetworkStack207 = new ECSNetworkStack207(this, "ecs-network",
                vpcStack101, basicSubnetsStack102);

        ALBStack207 albStack207 = new ALBStack207(this, "ecs-alb",
                vpcStack101, basicSubnetsStack102, ecsNetworkStack207);

        ECSStack207 ecsStack207 = new ECSStack207(this, "ecs",
                vpcStack101, basicSubnetsStack102, ecsNetworkStack207, albStack207);

    }

}

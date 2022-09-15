package gmi.workouts.computing.workout207;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class ECSParentStack extends Stack {

    public ECSParentStack(final Construct scope, final String id, final StackProps props,
                          final VpcStack vpcStack,
                          final BasicSubnetsStack subnetsStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(subnetsStack);

        ECSNetworkStack ecsNetworkStack = new ECSNetworkStack(this, "cpu-207-ecs-network", vpcStack, subnetsStack);

        ALBStack albStack = new ALBStack(this, "cpu-207-ecs-alb", vpcStack, subnetsStack, ecsNetworkStack);

        new ECSStack(this, "cpu-207-ecs", vpcStack, subnetsStack, ecsNetworkStack, albStack);

    }

}

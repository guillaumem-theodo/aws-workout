package gmi.workouts.computing.workout205;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class ALBParentStack extends Stack {

    public ALBParentStack(final Construct scope, final String id, final StackProps props,
                          final VpcStack vpcStack,
                          final BasicSubnetsStack subnetsStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(subnetsStack);

        ALBNetworkStack albNetworkStack = new ALBNetworkStack(this, "cpu-205-alb-network", vpcStack, subnetsStack);

        new ALBStack(this, "cpu-205-alb", vpcStack, subnetsStack, albNetworkStack);

    }

}

package gmi.workouts.computing.workout206;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class ASGParentStack206 extends Stack {

    public ASGParentStack206(final Construct scope, final String id, final StackProps props,
                             VpcStack vpcStack,
                             BasicSubnetsStack subnetsStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(subnetsStack);

        ASGNetworkStack asgNetworkStack = new ASGNetworkStack(this, "cpu-206-asg-network", vpcStack, subnetsStack);

        new ASGStack(this, "cpu-206-asg", vpcStack, subnetsStack, asgNetworkStack);

    }

}

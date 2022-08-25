package gmi.workouts.computing.workout206;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class ASGParentStack206 extends Stack {

    public ASGParentStack206(final Construct scope, final String id, final StackProps props,
                             VpcStack101 vpcStack101,
                             BasicSubnetsStack102 basicSubnetsStack102) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(basicSubnetsStack102);

        ASGNetworkStack206 asgNetworkStack206 = new ASGNetworkStack206(this, "asg-network",
                vpcStack101, basicSubnetsStack102);

        ASGStack206 asgStack206 = new ASGStack206(this, "asg",
                vpcStack101, basicSubnetsStack102, asgNetworkStack206);

    }

}

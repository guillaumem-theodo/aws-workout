package gmi.workouts.computing.workout205;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class ALBParentStack205 extends Stack {

    public ALBParentStack205(final Construct scope, final String id, final StackProps props,
                             VpcStack101 vpcStack101,
                             BasicSubnetsStack102 basicSubnetsStack102) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(basicSubnetsStack102);

        ALBNetworkStack205 albNetworkStack205 = new ALBNetworkStack205(this, "alb-network",
                vpcStack101, basicSubnetsStack102);

        ALBStack205 albStack205 = new ALBStack205(this, "alb",
                vpcStack101, basicSubnetsStack102, albNetworkStack205);

    }

}

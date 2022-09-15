package gmi.workouts.computing.workout208;

import gmi.workouts.common.S3ForTestsStack;
import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class ParentStack extends Stack {

    public ParentStack(final Construct scope, final String id, final StackProps props,
                       final VpcStack vpcStack,
                       final BasicSubnetsStack subnetsStack,
                       final S3ForTestsStack s3InFirstRegionStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(subnetsStack);

        LambdaStack lambdaStack = new LambdaStack(this, "cpu-208-lambda-stack", s3InFirstRegionStack);
        new DeploymentStack(this, "cpu-208-deployment-stack", lambdaStack);
        new EC2Stack(this, "cpu-208-ec2-stack", vpcStack, subnetsStack);
    }

}

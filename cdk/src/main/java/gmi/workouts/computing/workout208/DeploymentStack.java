package gmi.workouts.computing.workout208;

import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.services.apigateway.CfnDeployment;
import software.constructs.Construct;

public class DeploymentStack extends NestedStack {

    public DeploymentStack(final Construct scope, final String id, final LambdaStack lambdaStack) {
        super(scope, id);
        addDependency(lambdaStack);

        createDeployment(lambdaStack);
    }

    private void createDeployment(LambdaStack lambdaStack) {
        CfnDeployment.Builder.create(this, "cpu-208-api-gtw-deployment")
                .restApiId(lambdaStack.getApi().getRef())
                .stageName("dev")
                .build();
    }

}

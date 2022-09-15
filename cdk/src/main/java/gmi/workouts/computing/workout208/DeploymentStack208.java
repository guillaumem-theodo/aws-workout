package gmi.workouts.computing.workout208;

import gmi.workouts.common.S3ForTestsStack;
import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.utils.InternetGatewayHelper;
import gmi.workouts.utils.SecurityGroupHelper;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.CfnDeployment;
import software.amazon.awscdk.services.apigateway.CfnMethod;
import software.amazon.awscdk.services.apigateway.CfnResource;
import software.amazon.awscdk.services.apigateway.CfnRestApi;
import software.amazon.awscdk.services.ec2.CfnInternetGateway;
import software.amazon.awscdk.services.ec2.CfnSecurityGroup;
import software.amazon.awscdk.services.ec2.CfnSecurityGroupIngress;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Permission;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Map;

import static gmi.workouts.common.CommonIAM.LAMBDA_EXECUTION;
import static gmi.workouts.common.CommonIAM.READ_ONLY_S3_ACCESS;
import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.MyIpHelper.getMyIPAddressCIDR;
import static gmi.workouts.utils.SecurityGroupHelper.DefaultPort.HTTP;

public class DeploymentStack208 extends Stack {

    public DeploymentStack208(final Construct scope, final String id, final StackProps props,
                              LambdaStack208 lambdaStack) {
        super(scope, id, props);
        addDependency(lambdaStack);

        createDeployment(lambdaStack);
    }

    private void createDeployment(LambdaStack208 lambdaStack) {
        CfnDeployment.Builder.create(this, "cpu-208-api-gtw-deployment")
                .restApiId(lambdaStack.getApi().getRef())
                .stageName("dev")
                .build();

    }

}

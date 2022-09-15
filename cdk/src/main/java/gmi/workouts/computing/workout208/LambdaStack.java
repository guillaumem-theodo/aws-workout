package gmi.workouts.computing.workout208;

import gmi.workouts.common.S3ForTestsStack;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigateway.CfnMethod;
import software.amazon.awscdk.services.apigateway.CfnResource;
import software.amazon.awscdk.services.apigateway.CfnRestApi;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Permission;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Map;

import static gmi.workouts.utils.iam.IAMHelpers.LAMBDA_EXECUTION;
import static gmi.workouts.utils.iam.IAMHelpers.READ_ONLY_S3_ACCESS;

public class LambdaStack extends NestedStack {

    private final CfnRestApi cfnApi;

    public LambdaStack(final Construct scope, final String id,
                       final S3ForTestsStack s3InFirstRegionStack) {
        super(scope, id);
        addDependency(s3InFirstRegionStack);

        Function lambdaFunction = createLambdaFunction(s3InFirstRegionStack.getBucket());

        cfnApi = createApiGatewayAndApi(lambdaFunction);
    }

    private CfnRestApi createApiGatewayAndApi(Function lambdaFunction) {
        CfnRestApi cfnApi = CfnRestApi.Builder.create(this, "dev-cpu-208-api-gtw")
                .name("dev-cpu-208-api-gtw")
                .build();

        CfnResource cfnResource = CfnResource.Builder.create(this, "cpu-208-api-gtw-resource")
                .restApiId(cfnApi.getRef())
                .parentId(cfnApi.getAttrRootResourceId())
                .pathPart("demo-208")
                .build();

        CfnMethod.Builder.create(this, "cpu-208-api-gtw-method")
                .restApiId(cfnApi.getRef())
                .resourceId(cfnResource.getAttrResourceId())
                .httpMethod("GET")
                .authorizationType("NONE")
                .integration(
                        CfnMethod.IntegrationProperty.builder()
                                .integrationHttpMethod("POST")
                                .type("AWS_PROXY")
                                .uri(getLambdaInvocationUri(lambdaFunction))
                                .build())
                .build();

        authorizeApiGtwToInvokeLambda(lambdaFunction, cfnApi);

        return cfnApi;
    }

    private void authorizeApiGtwToInvokeLambda(Function lambdaFunction, CfnRestApi cfnApi) {
        String region = Stack.of(this).getRegion();
        String account = Stack.of(this).getAccount();

        lambdaFunction.addPermission("AllowExecutionFromAPIGateway",
                Permission.builder()
                        .action("lambda:InvokeFunction")
                        .principal(ServicePrincipal.Builder.create("apigateway.amazonaws.com").build())
                        .sourceArn("arn:aws:execute-api:" + region + ":" + account + ":" + cfnApi.getRef() + "/*/GET/demo-208")
                        .build());
    }

    private Function createLambdaFunction(Bucket bucket) {
        Role role = Role.Builder.create(this, "cpu-208-role-for-lambda")
                .assumedBy(ServicePrincipal.Builder.create("lambda.amazonaws.com").build())
                .managedPolicies(Arrays.asList(
                        ManagedPolicy.fromManagedPolicyArn(this, "cpu-208-policy-1", READ_ONLY_S3_ACCESS),
                        ManagedPolicy.fromManagedPolicyArn(this, "cpu-208-policy-2", LAMBDA_EXECUTION)
                ))
                .roleName("cpu-208-role-for-lambda")
                .build();

        return Function.Builder.create(this, "cpu-208-lambda-1")
                .functionName("cpu-208-lambda-1")
                .role(role)
                .handler("handler.listAllObjects")
                .code(Code.fromAsset("../2-computing/208-sls-lambda/sls/.serverless/cpu-208-sls-lambda.zip"))
                .runtime(Runtime.NODEJS_12_X)
                .environment(Map.of("BUCKET_NAME", bucket.getBucketName()))
                .build();
    }

    public CfnRestApi getApi() {
        return cfnApi;
    }

    private String getLambdaInvocationUri(Function lambdaFunction) {
        String region = Stack.of(this).getRegion();
        return "arn:aws:apigateway:" + region + ":lambda:path/2015-03-31/functions/" + lambdaFunction.getFunctionArn() + "/invocations";
    }
}

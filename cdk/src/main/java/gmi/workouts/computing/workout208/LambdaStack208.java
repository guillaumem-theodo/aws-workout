package gmi.workouts.computing.workout208;

import gmi.workouts.common.S3ForTestsStack;
import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.utils.EC2Helper;
import gmi.workouts.utils.InternetGatewayHelper;
import gmi.workouts.utils.SecurityGroupHelper;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.CfnRefElement;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;

import software.amazon.awscdk.services.ec2.CfnInternetGateway;
import software.amazon.awscdk.services.ec2.CfnSecurityGroup;
import software.amazon.awscdk.services.ec2.CfnSecurityGroupIngress;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
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

import static gmi.workouts.common.CommonIAM.*;
import static gmi.workouts.utils.EC2Helper.createEC2;
import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.MyIpHelper.getMyIPAddressCIDR;
import static gmi.workouts.utils.SecurityGroupHelper.DefaultPort.HTTP;

public class LambdaStack208 extends Stack {

    private CfnRestApi cfnApi;

    public LambdaStack208(final Construct scope, final String id, final StackProps props,
                          VpcStack101 vpcStack101,
                          BasicSubnetsStack102 basicSubnetsStack102,
                          S3ForTestsStack s3ForTestsInFirstRegionStack,
                          S3ForTestsStack s3ForTestsInSecondRegionStack) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(basicSubnetsStack102);
        addDependency(s3ForTestsInFirstRegionStack);
        addDependency(s3ForTestsInSecondRegionStack);

        CfnSecurityGroup securityGroup = createNetwork(vpcStack101, basicSubnetsStack102);
        CfnInstanceProfile instanceProfile = createCommonEC2InstanceProfile(this);
        createEC2(this, "cpu-208-ec2-test-1",
                basicSubnetsStack102.getSubnet2(), securityGroup, EC2Helper.Ip.WITH_PUBLIC_IP,
                builder -> builder.iamInstanceProfile(instanceProfile.getInstanceProfileName()));

        Function lambdaFunction = createLambdaFunction(s3ForTestsInFirstRegionStack);

        createApiGatewayAndApi(lambdaFunction);
    }

    private void createApiGatewayAndApi(Function lambdaFunction) {
        cfnApi = CfnRestApi.Builder.create(this, "dev-cpu-208-api-gtw")
                .name("dev-cpu-208-api-gtw")
                .build();

        CfnResource cfnResource = CfnResource.Builder.create(this, "cpu-208-api-gtw-resource")
                .restApiId(cfnApi.getRef())
                .parentId(cfnApi.getAttrRootResourceId())
                .pathPart("demo-208")
                .build();

        CfnMethod cfnMethod = CfnMethod.Builder.create(this, "cpu-208-api-gtw-method")
                .restApiId(cfnApi.getRef())
                .resourceId(cfnResource.getAttrResourceId())
                .httpMethod("GET")
                .authorizationType("NONE")
                .integration(
                        CfnMethod.IntegrationProperty.builder()
                                .integrationHttpMethod("POST")
                                .type("AWS_PROXY")
                                .uri("arn:aws:apigateway:"+Stack.of(this).getRegion()+":lambda:path/2015-03-31/functions/"+lambdaFunction.getFunctionArn()+"/invocations")
                        .build())
                .build();

        lambdaFunction.addPermission("AllowExecutionFromAPIGateway",
                Permission.builder()
                        .action("lambda:InvokeFunction")
                        .principal(ServicePrincipal.Builder.create("apigateway.amazonaws.com").build())
                        .sourceArn("arn:aws:execute-api:"+Stack.of(this).getRegion()+":"+Stack.of(this).getAccount()+":"+ cfnApi.getRef()+"/*/GET/demo-208")
                .build());

    }

    private Function createLambdaFunction(S3ForTestsStack s3ForTestsInFirstRegionStack) {
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
                .environment(Map.of("BUCKET_NAME", s3ForTestsInFirstRegionStack.getBucket().getBucketName() ))
                .build();

    }

    private @NotNull CfnSecurityGroup createNetwork(VpcStack101 vpcStack101, BasicSubnetsStack102 basicSubnetsStack102) {
        CfnSubnet subnet2 = basicSubnetsStack102.getSubnet2();

        CfnInternetGateway internetGateway = createAndAttachInternetGateway(this, vpcStack101.getVpc(), "cpu-208-igw");
        InternetGatewayHelper.createAndAttachRouteTableToSubnets(this, "cpu-208-rt-1", vpcStack101.getVpc(), internetGateway, subnet2);

        return createSecurityGroup(vpcStack101);
    }

    @NotNull
    private  CfnSecurityGroup createSecurityGroup(VpcStack101 vpcStack101) {
        CfnSecurityGroup securityGroup = SecurityGroupHelper.createSecurityGroup(this, vpcStack101.getVpc(), "cpu-208-sg", HTTP);

        CfnSecurityGroupIngress.Builder.create(this, "cpu-208-sg-rule-ssh")
                .groupId(securityGroup.getAttrGroupId())
                .fromPort(22).toPort(22).ipProtocol("tcp")
                .cidrIp(getMyIPAddressCIDR())
                .build();
        CfnSecurityGroupIngress.Builder.create(this, "cpu-208-sg-rule-ping")
                .groupId(securityGroup.getAttrGroupId())
                .fromPort(-1).toPort(-1).ipProtocol("icmp")
                .cidrIp(getMyIPAddressCIDR())
                .build();
        return securityGroup;
    }

    public CfnRestApi getApi() {
        return cfnApi;
    }
}

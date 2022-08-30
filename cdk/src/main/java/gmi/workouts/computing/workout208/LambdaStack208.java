package gmi.workouts.computing.workout208;

import gmi.workouts.common.S3ForTestsStack;
import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.utils.EC2Helper;
import gmi.workouts.utils.InternetGatewayHelper;
import gmi.workouts.utils.SecurityGroupHelper;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnInternetGateway;
import software.amazon.awscdk.services.ec2.CfnSecurityGroup;
import software.amazon.awscdk.services.ec2.CfnSecurityGroupIngress;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.constructs.Construct;

import static gmi.workouts.common.CommonIAM.createCommonEC2InstanceProfile;
import static gmi.workouts.utils.EC2Helper.createEC2;
import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.MyIpHelper.getMyIPAddressCIDR;
import static gmi.workouts.utils.SecurityGroupHelper.DefaultPort.HTTP;

public class LambdaStack208 extends Stack {

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


        CfnOutput.Builder.create(this, "cpu-208-bucket1")
                .value(s3ForTestsInFirstRegionStack.getBucket().getBucketName())
                .build();
        CfnOutput.Builder.create(this, "cpu-208-bucket2")
                .value(s3ForTestsInSecondRegionStack.getBucket().getBucketName())
                .build();    }

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

}

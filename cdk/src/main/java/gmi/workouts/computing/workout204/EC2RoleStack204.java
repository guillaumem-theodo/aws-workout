package gmi.workouts.computing.workout204;

import gmi.workouts.common.S3ForTestsStack;
import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.utils.EC2Helper;
import gmi.workouts.utils.InternetGatewayHelper;
import gmi.workouts.utils.SecurityGroupHelper;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.constructs.Construct;

import java.util.Collections;

import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.IpChecker.getMyIPAddressCIDR;
import static gmi.workouts.utils.SecurityGroupHelper.DefaultPort.HTTP;
import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class EC2RoleStack204 extends Stack {

    public EC2RoleStack204(final Construct scope, final String id, final StackProps props,
                           VpcStack101 vpcStack101,
                           BasicSubnetsStack102 basicSubnetsStack102,
                           S3ForTestsStack s3ForTestsInFirstRegionStack, S3ForTestsStack s3ForTestsInSecondRegionStack) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(basicSubnetsStack102);
        addDependency(s3ForTestsInFirstRegionStack);
        addDependency(s3ForTestsInSecondRegionStack);

        CfnSecurityGroup securityGroup = createNetwork(vpcStack101, basicSubnetsStack102);

        Role role = Role.Builder.create(this, "cpu-204-iam-role-1")
                .assumedBy(ServicePrincipal.Builder.create("ec2.amazonaws.com").build())
                .managedPolicies(Collections.singletonList(
                        ManagedPolicy.fromManagedPolicyArn(this, "cpu-204-policy-attached-to-role",
                                "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess")))
                .roleName("cpu-204-iam-role-1")
                .build();


        CfnInstanceProfile instanceProfile = CfnInstanceProfile.Builder.create(this, "cpu-204-instance-profile-1")
                .roles(Collections.singletonList(role.getRoleName()))
                .instanceProfileName("cpu-204-instance-profile-1")
                .build();


        IMachineImage latestAMI = MachineImage.fromSsmParameter(EC2Helper.LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2, null);
        CfnInstance.Builder.create(this, "cpu-204-ec2-1")
                .imageId(latestAMI.getImage(this).getImageId())
                .keyName("aws-workout-key")
                .instanceType("t2.micro")
                .iamInstanceProfile(instanceProfile.getInstanceProfileName())
                .networkInterfaces(
                        Collections.singletonList(
                                CfnInstance.NetworkInterfaceProperty.builder()
                                        .subnetId(basicSubnetsStack102.getSubnet2().getAttrSubnetId())
                                        .associatePublicIpAddress(true)
                                        .groupSet(Collections.singletonList(securityGroup.getAttrGroupId()))
                                        .deviceIndex("0").build()

                        ))

                .tags(createCommonTags("cpu-204-ec2-1"))
                .build();

    }

    private @NotNull CfnSecurityGroup createNetwork(VpcStack101 vpcStack101, BasicSubnetsStack102 basicSubnetsStack102) {
        CfnSubnet subnet2 = basicSubnetsStack102.getSubnet2();

        CfnInternetGateway internetGateway = createAndAttachInternetGateway(this, vpcStack101.getVpc(), "cpu-204-igw");
        InternetGatewayHelper.createAndAttachRouteTableToSubnet(this, vpcStack101.getVpc(), subnet2, internetGateway, "cpu-204-rt-1");

        return createSecurityGroup(vpcStack101);
    }

    @NotNull
    private  CfnSecurityGroup createSecurityGroup(VpcStack101 vpcStack101) {
        CfnSecurityGroup securityGroup = SecurityGroupHelper.createSecurityGroup(this, vpcStack101.getVpc(), "cpu-204-sg", HTTP);

        CfnSecurityGroupIngress.Builder.create(this, "cpu-204-sg-rule-ssh")
                .groupId(securityGroup.getAttrGroupId())
                .fromPort(22).toPort(22).ipProtocol("tcp")
                .cidrIp(getMyIPAddressCIDR())
                .build();
        CfnSecurityGroupIngress.Builder.create(this, "cpu-204-sg-rule-ping")
                .groupId(securityGroup.getAttrGroupId())
                .fromPort(-1).toPort(-1).ipProtocol("icmp")
                .cidrIp(getMyIPAddressCIDR())
                .build();
        return securityGroup;
    }

}

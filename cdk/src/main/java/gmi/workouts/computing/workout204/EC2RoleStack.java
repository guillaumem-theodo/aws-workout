package gmi.workouts.computing.workout204;

import gmi.workouts.common.S3ForTestsStack;
import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import gmi.workouts.utils.compute.EC2Helper;
import gmi.workouts.utils.compute.SecurityGroupHelper;
import gmi.workouts.utils.network.InternetGatewayHelper;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.constructs.Construct;

import java.util.Collections;

import static gmi.workouts.utils.TagsHelper.createCommonTags;
import static gmi.workouts.utils.compute.SecurityGroupHelper.DefaultPort.HTTP;
import static gmi.workouts.utils.network.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.network.MyIpHelper.getMyIPAddressCIDR;

public class EC2RoleStack extends Stack {

    public EC2RoleStack(final Construct scope, final String id, final StackProps props,
                        final VpcStack vpcStack,
                        final BasicSubnetsStack subnetsStack,
                        final S3ForTestsStack s3InFirstRegionStack,
                        final S3ForTestsStack s3InSecondRegionStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(subnetsStack);
        addDependency(s3InFirstRegionStack);
        addDependency(s3InSecondRegionStack);

        CfnSecurityGroup securityGroup = createNetwork(vpcStack, subnetsStack);

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
                                        .subnetId(subnetsStack.getSubnet2().getAttrSubnetId())
                                        .associatePublicIpAddress(true)
                                        .groupSet(Collections.singletonList(securityGroup.getAttrGroupId()))
                                        .deviceIndex("0").build()

                        ))

                .tags(createCommonTags("cpu-204-ec2-1"))
                .build();

    }

    private CfnSecurityGroup createNetwork(VpcStack vpcStack, BasicSubnetsStack basicSubnetsStack) {
        CfnVPC vpc = vpcStack.getVpc();
        CfnSubnet subnet = basicSubnetsStack.getSubnet2();

        CfnInternetGateway igw = createAndAttachInternetGateway(this, vpc, "cpu-204-igw");
        InternetGatewayHelper.createAndAttachRouteTableToSubnets(this, "cpu-204-rt-1", vpc, igw, subnet);

        return createSecurityGroup(vpc);
    }

    private  CfnSecurityGroup createSecurityGroup(CfnVPC vpc) {
        CfnSecurityGroup securityGroup = SecurityGroupHelper.createSecurityGroup(this, vpc, "cpu-204-sg", HTTP);

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

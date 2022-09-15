package gmi.workouts.computing.workout208;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import gmi.workouts.utils.compute.SecurityGroupHelper;
import gmi.workouts.utils.network.InternetGatewayHelper;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.constructs.Construct;

import static gmi.workouts.utils.compute.EC2Helper.Ip.WITH_PUBLIC_IP;
import static gmi.workouts.utils.compute.EC2Helper.createEC2;
import static gmi.workouts.utils.compute.SecurityGroupHelper.DefaultPort.HTTP;
import static gmi.workouts.utils.iam.IAMHelpers.createEC2InstanceProfile;
import static gmi.workouts.utils.network.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.network.MyIpHelper.getMyIPAddressCIDR;

public class EC2Stack extends NestedStack {

    public EC2Stack(final Construct scope, final String id,
                    final VpcStack vpcStack,
                    final BasicSubnetsStack subnetsStack) {
        super(scope, id);
        addDependency(vpcStack);
        addDependency(subnetsStack);

        CfnSecurityGroup securityGroup = createNetwork(vpcStack, subnetsStack);
         CfnInstanceProfile instanceProfile = createEC2InstanceProfile(this, "cpu-208-ec2");
         createEC2(this, "cpu-208-ec2-test-1",
                 subnetsStack.getSubnet2(), securityGroup, WITH_PUBLIC_IP,
                 builder -> builder.iamInstanceProfile(instanceProfile.getInstanceProfileName()));

    }
    private CfnSecurityGroup createNetwork(VpcStack vpcStack, BasicSubnetsStack basicSubnetsStack) {
        CfnSubnet subnet = basicSubnetsStack.getSubnet2();
        CfnVPC vpc = vpcStack.getVpc();

        CfnInternetGateway igw = createAndAttachInternetGateway(this, vpc, "cpu-208-igw");
        InternetGatewayHelper.createAndAttachRouteTableToSubnets(this, "cpu-208-rt-1", vpc, igw, subnet);

        return createSecurityGroup(vpc);
    }

    private  CfnSecurityGroup createSecurityGroup(CfnVPC vpc) {
        CfnSecurityGroup securityGroup = SecurityGroupHelper.createSecurityGroup(this, vpc, "cpu-208-sg", HTTP);

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

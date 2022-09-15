package gmi.workouts.computing.workout207;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import gmi.workouts.utils.compute.SecurityGroupHelper;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import static gmi.workouts.utils.compute.SecurityGroupHelper.DefaultPort.HTTP;
import static gmi.workouts.utils.network.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.network.InternetGatewayHelper.createAndAttachRouteTableToSubnets;
import static gmi.workouts.utils.network.MyIpHelper.getMyIPAddressCIDR;
import static gmi.workouts.utils.network.NatGatewayHelper.createAndAttachNatGateway;

public class ECSNetworkStack extends NestedStack {

    private CfnSecurityGroup albSecurityGroup;
    private CfnSecurityGroup bastionSecurityGroup;
    private CfnSecurityGroup workerSecurityGroup;

    public ECSNetworkStack(final Construct scope, final String id,
                           final VpcStack vpcStack,
                           final BasicSubnetsStack basicSubnetsStack) {
        super(scope, id);
        addDependency(vpcStack);
        addDependency(basicSubnetsStack);

        createNetwork(vpcStack, basicSubnetsStack);
    }


    private void createNetwork(VpcStack vpcStack, BasicSubnetsStack basicSubnetsStack) {
        CfnSubnet subnet1 = basicSubnetsStack.getSubnet1();
        CfnSubnet subnet2 = basicSubnetsStack.getSubnet2();
        CfnSubnet subnet3 = basicSubnetsStack.getSubnet3();
        CfnSubnet subnet4 = basicSubnetsStack.getSubnet4();

        CfnVPC vpc = vpcStack.getVpc();
        CfnInternetGateway igw = createAndAttachInternetGateway(this, vpc, "cpu-207-igw");
        createAndAttachRouteTableToSubnets(this, "cpu-207-rt-1", vpc, igw, subnet1, subnet2);
        createAndAttachNatGateway(this, "cpu-207-nat", vpc, subnet1, subnet3, subnet4);
        createSecurityGroup(vpc);
    }

    private void createSecurityGroup(CfnVPC vpc) {
        albSecurityGroup = SecurityGroupHelper.createSecurityGroup(this, vpc, "cpu-207-sg-1", HTTP);

        bastionSecurityGroup = SecurityGroupHelper.createSecurityGroup(this, vpc, "cpu-207-sg-2");

        workerSecurityGroup = SecurityGroupHelper.createSecurityGroup(this, vpc, "cpu-207-sg-3");

        CfnSecurityGroupIngress.Builder.create(this, "cpu-207-sg-2-ssh")
                .groupId(bastionSecurityGroup.getAttrGroupId())
                .fromPort(22).toPort(22).ipProtocol("tcp")
                .cidrIp(getMyIPAddressCIDR())
                .build();

        CfnSecurityGroupIngress.Builder.create(this, "cpu-207-sg-3-ssh")
                .groupId(workerSecurityGroup.getAttrGroupId())
                .fromPort(22).toPort(22).ipProtocol("tcp")
                .sourceSecurityGroupId(bastionSecurityGroup.getAttrGroupId())
                .build();

        CfnSecurityGroupIngress.Builder.create(this, "cpu-207-sg-3-http")
                .groupId(workerSecurityGroup.getAttrGroupId())
                .fromPort(80).toPort(80).ipProtocol("tcp")
                .sourceSecurityGroupId(albSecurityGroup.getAttrGroupId())
                .build();
    }

    public CfnSecurityGroup getAlbSecurityGroup() {
        return albSecurityGroup;
    }

    public CfnSecurityGroup getWorkerSecurityGroup() {
        return workerSecurityGroup;
    }

}

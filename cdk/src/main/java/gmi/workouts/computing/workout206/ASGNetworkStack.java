package gmi.workouts.computing.workout206;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import gmi.workouts.utils.compute.SecurityGroupHelper;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.services.ec2.CfnInternetGateway;
import software.amazon.awscdk.services.ec2.CfnSecurityGroup;
import software.amazon.awscdk.services.ec2.CfnSecurityGroupIngress;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.constructs.Construct;

import static gmi.workouts.utils.compute.SecurityGroupHelper.DefaultPort.HTTP;
import static gmi.workouts.utils.network.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.network.InternetGatewayHelper.createAndAttachRouteTableToSubnets;
import static gmi.workouts.utils.network.MyIpHelper.getMyIPAddressCIDR;
import static gmi.workouts.utils.network.NatGatewayHelper.createAndAttachNatGateway;

public class ASGNetworkStack extends NestedStack {

    private CfnSecurityGroup albSecurityGroup;
    private CfnSecurityGroup bastionSecurityGroup;
    private CfnSecurityGroup workerSecurityGroup;


    public ASGNetworkStack(final Construct scope, final String id,
                           final VpcStack vpcStack,
                           final BasicSubnetsStack subnetsStack) {
        super(scope, id);
        addDependency(vpcStack);
        addDependency(subnetsStack);

        createNetwork(vpcStack, subnetsStack);
    }


    private void createNetwork(VpcStack vpcStack, BasicSubnetsStack basicSubnetsStack) {
        CfnSubnet subnet1 = basicSubnetsStack.getSubnet1();
        CfnSubnet subnet2 = basicSubnetsStack.getSubnet2();
        CfnSubnet subnet3 = basicSubnetsStack.getSubnet3();
        CfnSubnet subnet4 = basicSubnetsStack.getSubnet4();

        CfnInternetGateway internetGateway = createAndAttachInternetGateway(this, vpcStack.getVpc(), "cpu-206-igw");
        createAndAttachRouteTableToSubnets(this, "cpu-206-rt-1", vpcStack.getVpc(), internetGateway, subnet1, subnet2);
        createAndAttachNatGateway(this, "cpu-206-nat", vpcStack.getVpc(), subnet1, subnet3, subnet4);

        createSecurityGroup(vpcStack);
    }

    private void createSecurityGroup(VpcStack vpcStack) {
        albSecurityGroup = SecurityGroupHelper.createSecurityGroup(this, vpcStack.getVpc(), "cpu-206-sg-1", HTTP);

        bastionSecurityGroup = SecurityGroupHelper.createSecurityGroup(this, vpcStack.getVpc(), "cpu-206-sg-2");

        workerSecurityGroup = SecurityGroupHelper.createSecurityGroup(this, vpcStack.getVpc(), "cpu-206-sg-3");

        CfnSecurityGroupIngress.Builder.create(this, "cpu-206-sg-2-ssh")
                .groupId(bastionSecurityGroup.getAttrGroupId())
                .fromPort(22).toPort(22).ipProtocol("tcp")
                .cidrIp(getMyIPAddressCIDR())
                .build();

        CfnSecurityGroupIngress.Builder.create(this, "cpu-206-sg-3-ssh")
                .groupId(workerSecurityGroup.getAttrGroupId())
                .fromPort(22).toPort(22).ipProtocol("tcp")
                .sourceSecurityGroupId(bastionSecurityGroup.getAttrGroupId())
                .build();

        CfnSecurityGroupIngress.Builder.create(this, "cpu-206-sg-3-http")
                .groupId(workerSecurityGroup.getAttrGroupId())
                .fromPort(80).toPort(80).ipProtocol("tcp")
                .sourceSecurityGroupId(albSecurityGroup.getAttrGroupId())
                .build();
    }

    public CfnSecurityGroup getAlbSecurityGroup() {
        return albSecurityGroup;
    }

    public CfnSecurityGroup getBastionSecurityGroup() {
        return bastionSecurityGroup;
    }

    public CfnSecurityGroup getWorkerSecurityGroup() {
        return workerSecurityGroup;
    }

}

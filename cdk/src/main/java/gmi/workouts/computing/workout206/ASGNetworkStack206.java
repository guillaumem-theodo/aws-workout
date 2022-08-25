package gmi.workouts.computing.workout206;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.utils.SecurityGroupHelper;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.services.ec2.CfnInternetGateway;
import software.amazon.awscdk.services.ec2.CfnSecurityGroup;
import software.amazon.awscdk.services.ec2.CfnSecurityGroupIngress;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.constructs.Construct;

import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachRouteTableToSubnets;
import static gmi.workouts.utils.MyIpHelper.getMyIPAddressCIDR;
import static gmi.workouts.utils.NatGatewayHelper.createAndAttachNatGateway;
import static gmi.workouts.utils.SecurityGroupHelper.DefaultPort.HTTP;

public class ASGNetworkStack206 extends NestedStack {

    private CfnSecurityGroup albSecurityGroup;
    private CfnSecurityGroup bastionSecurityGroup;
    private CfnSecurityGroup workerSecurityGroup;


    public ASGNetworkStack206(final Construct scope, final String id,
                              VpcStack101 vpcStack101,
                              BasicSubnetsStack102 basicSubnetsStack102) {
        super(scope, id);
        addDependency(vpcStack101);
        addDependency(basicSubnetsStack102);

        createNetwork(vpcStack101, basicSubnetsStack102);
    }


    private void createNetwork(VpcStack101 vpcStack101, BasicSubnetsStack102 basicSubnetsStack102) {
        CfnSubnet subnet1 = basicSubnetsStack102.getSubnet1();
        CfnSubnet subnet2 = basicSubnetsStack102.getSubnet2();
        CfnSubnet subnet3 = basicSubnetsStack102.getSubnet3();
        CfnSubnet subnet4 = basicSubnetsStack102.getSubnet4();

        CfnInternetGateway internetGateway = createAndAttachInternetGateway(this, vpcStack101.getVpc(), "cpu-206-igw");
        createAndAttachRouteTableToSubnets(this, "cpu-206-rt-1", vpcStack101.getVpc(), internetGateway, subnet1, subnet2);
        createAndAttachNatGateway(this, "cpu-206-nat", vpcStack101.getVpc(), subnet1, subnet3, subnet4);

        createSecurityGroup(vpcStack101);
    }

    private void createSecurityGroup(VpcStack101 vpcStack101) {
        albSecurityGroup = SecurityGroupHelper.createSecurityGroup(this, vpcStack101.getVpc(), "cpu-206-sg-1", HTTP);

        bastionSecurityGroup = SecurityGroupHelper.createSecurityGroup(this, vpcStack101.getVpc(), "cpu-206-sg-2");

        workerSecurityGroup = SecurityGroupHelper.createSecurityGroup(this, vpcStack101.getVpc(), "cpu-206-sg-3");

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

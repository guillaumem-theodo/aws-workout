package gmi.workouts.networking.workout109;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.constructs.Construct;

import static gmi.workouts.utils.TagsHelper.createCommonTags;
import static gmi.workouts.utils.compute.EC2Helper.Ip.WITH_PUBLIC_IP;
import static gmi.workouts.utils.compute.EC2Helper.createEC2;
import static gmi.workouts.utils.compute.SecurityGroupHelper.DefaultPort.SSH;
import static gmi.workouts.utils.compute.SecurityGroupHelper.createSecurityGroup;
import static gmi.workouts.utils.iam.IAMHelpers.createEC2InstanceProfile;

public class VpcPeeringStack extends Stack {


    private final VpcPeeringNetworkStack peeringNetworkStack;
    private CfnSecurityGroup sg1;
    private CfnSecurityGroup sg2;
    private CfnSecurityGroup sg3;

    public VpcPeeringStack(final Construct scope, final String id, final StackProps props, final VpcPeeringNetworkStack peeringNetworkStack) {
        super(scope, id, props);
        this.peeringNetworkStack = peeringNetworkStack;
        addDependency(peeringNetworkStack);


        createEC2InEachVPC();

        createVpcPeering(peeringNetworkStack.getVpc3(), peeringNetworkStack.getVpc2(),
                peeringNetworkStack.getRouteTable3(), peeringNetworkStack.getRouteTable2(),
                sg3, sg2, "net-109-peering-3-2");

        CfnVPCPeeringConnection vpcPeering = createVpcPeering(peeringNetworkStack.getVpc2(), peeringNetworkStack.getVpc1(),
                peeringNetworkStack.getRouteTable2(), peeringNetworkStack.getRouteTable1(),
                sg2, sg1, "net-109-peering-2-1");

        new VpcPeeringChangeDNSResolutionConstruct(this, "net-109-peering-2-1-options", vpcPeering);
    }

    private void createEC2InEachVPC() {

        sg1 = createSecurityGroup(this, peeringNetworkStack.getVpc1(), "net-109-sg-1");
        sg2 = createSecurityGroup(this, peeringNetworkStack.getVpc2(), "net-109-sg-2");
        sg3 = createSecurityGroup(this, peeringNetworkStack.getVpc3(), "net-109-sg-3", SSH);

        CfnSecurityGroupIngress.Builder.create(this, "net-109-sg-3-2")
                .groupId(sg3.getAttrGroupId())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .cidrIp(peeringNetworkStack.getVpc2().getCidrBlock())
                .build();

        CfnSecurityGroupIngress.Builder.create(this, "nnet-109-sg-2-3")
                .groupId(sg2.getAttrGroupId())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .cidrIp(peeringNetworkStack.getVpc3().getCidrBlock())
                .build();

        CfnSecurityGroupIngress.Builder.create(this, "net-109-sg-1-2")
                .groupId(sg1.getAttrGroupId())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .cidrIp(peeringNetworkStack.getVpc2().getCidrBlock())
                .build();

        CfnInstanceProfile instanceProfile = createEC2InstanceProfile(this, "net-109-ec2");
        createEC2(this, "net-109-ec2-1", peeringNetworkStack.getSubnet1(), sg1, WITH_PUBLIC_IP,
                        builder -> builder.iamInstanceProfile(instanceProfile.getInstanceProfileName()));
        createEC2(this, "net-109-ec2-2", peeringNetworkStack.getSubnet2(), sg2, WITH_PUBLIC_IP,
                        builder -> builder.iamInstanceProfile(instanceProfile.getInstanceProfileName()));
        createEC2(this, "net-109-ec2-3", peeringNetworkStack.getSubnet3(), sg3, WITH_PUBLIC_IP,
                        builder -> builder.iamInstanceProfile(instanceProfile.getInstanceProfileName()));
    }

    private CfnVPCPeeringConnection createVpcPeering(CfnVPC vpcA, CfnVPC vpcB,
                                                     CfnRouteTable routeTableA, CfnRouteTable routeTableB,
                                                     CfnSecurityGroup securityGroupA, CfnSecurityGroup securityGroupB,
                                                     String name) {
        CfnVPCPeeringConnection peeringConnection = CfnVPCPeeringConnection.Builder.create(this, name)
                .vpcId(vpcA.getAttrVpcId())
                .peerVpcId(vpcB.getAttrVpcId())
                .tags(createCommonTags(name))
                .build();

        CfnRoute.Builder.create(this, name + "route-from-a-to-b")
                .routeTableId(routeTableA.getAttrRouteTableId())
                .destinationCidrBlock(vpcB.getCidrBlock())
                .vpcPeeringConnectionId(peeringConnection.getAttrId())
                .build();

        CfnRoute.Builder.create(this, name + "route-from-b-to-a")
                .routeTableId(routeTableB.getAttrRouteTableId())
                .destinationCidrBlock(vpcA.getCidrBlock())
                .vpcPeeringConnectionId(peeringConnection.getAttrId())
                .build();

        CfnSecurityGroupIngress.Builder.create(this, name + "sg-allow-access-to-A")
                .groupId(securityGroupB.getAttrGroupId())
                .cidrIp(vpcA.getCidrBlock())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .build();

        CfnSecurityGroupIngress.Builder.create(this, name + "sg-allow-access-to-B")
                .groupId(securityGroupA.getAttrGroupId())
                .cidrIp(vpcB.getCidrBlock())
                .fromPort(0).toPort(0).ipProtocol("-1")
                .build();

        return peeringConnection;
    }

}

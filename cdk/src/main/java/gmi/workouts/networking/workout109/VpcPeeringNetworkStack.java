package gmi.workouts.networking.workout109;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnInternetGateway;
import software.amazon.awscdk.services.ec2.CfnRouteTable;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.amazon.awscdk.services.ec2.CfnVPC;
import software.constructs.Construct;

import java.util.List;

import static gmi.workouts.utils.TagsHelper.createCommonTags;
import static gmi.workouts.utils.network.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.network.InternetGatewayHelper.createAndAttachRouteTableToSubnets;

public class VpcPeeringNetworkStack extends Stack {

    private final CfnVPC vpc1;
    private final CfnVPC vpc2;
    private final CfnVPC vpc3;
    private final CfnSubnet subnet1;
    private final CfnSubnet subnet2;
    private final CfnSubnet subnet3;
    private final CfnRouteTable routeTable1;
    private final CfnRouteTable routeTable2;
    private final CfnRouteTable routeTable3;
    private final CfnInternetGateway igw;

    public VpcPeeringNetworkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


        vpc1 = CfnVPC.Builder.create(this, "net-109-vpc-1")
                .cidrBlock("10.0.0.0/16")
                .enableDnsHostnames(true)
                .enableDnsSupport(true)
                .tags(createCommonTags("net-109-vpc-1"))
                .build();

        vpc2 = CfnVPC.Builder.create(this, "net-109-vpc-2")
                .cidrBlock("10.1.0.0/16")
                .enableDnsHostnames(true)
                .enableDnsSupport(true)
                .tags(createCommonTags("net-109-vpc-2"))
                .build();

        vpc3 = CfnVPC.Builder.create(this, "net-109-vpc-3")
                .cidrBlock("10.2.0.0/16")
                .enableDnsHostnames(true)
                .enableDnsSupport(true)
                .tags(createCommonTags("net-109-vpc-3"))
                .build();

        List<String> availabilityZones = Stack.of(this).getAvailabilityZones();
        String oneAZ = availabilityZones.get(0);

        subnet1 = CfnSubnet.Builder.create(this, "net-109-subnet-1")
                .cidrBlock("10.0.0.0/24")
                .vpcId(vpc1.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-109-subnet-1")).build();

        subnet2 = CfnSubnet.Builder.create(this, "net-109-subnet-2")
                .cidrBlock("10.1.0.0/24")
                .vpcId(vpc2.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-109-subnet-2")).build();

        subnet3 = CfnSubnet.Builder.create(this, "net-109-subnet-3")
                .cidrBlock("10.2.0.0/24")
                .vpcId(vpc3.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-109-subnet-3")).build();

        igw = createAndAttachInternetGateway(this, vpc3, "net-109-igw");

        routeTable1 = createAndAttachRouteTableToSubnets(this, "net-109-rt-1", vpc1, null, subnet1);
        routeTable2 = createAndAttachRouteTableToSubnets(this, "net-109-rt-2", vpc2, null, subnet2);
        routeTable3 = createAndAttachRouteTableToSubnets(this, "net-109-rt-3", vpc3, igw, subnet3);
    }

    public CfnVPC getVpc1() {
        return vpc1;
    }

    public CfnVPC getVpc2() {
        return vpc2;
    }

    public CfnVPC getVpc3() {
        return vpc3;
    }

    public CfnSubnet getSubnet1() {
        return subnet1;
    }

    public CfnSubnet getSubnet2() {
        return subnet2;
    }

    public CfnSubnet getSubnet3() {
        return subnet3;
    }

    public CfnInternetGateway getIgw() {
        return igw;
    }

    public CfnRouteTable getRouteTable3() {
        return routeTable3;
    }

    public CfnRouteTable getRouteTable2() {
        return routeTable2;
    }

    public CfnRouteTable getRouteTable1() {
        return routeTable1;
    }
}

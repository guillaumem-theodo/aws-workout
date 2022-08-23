package gmi.workouts.networking.workout108;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnInternetGateway;
import software.amazon.awscdk.services.ec2.CfnSecurityGroup;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.amazon.awscdk.services.ec2.CfnVPC;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.constructs.Construct;

import java.util.List;

import static gmi.workouts.common.CommonIAM.createCommonEC2InstanceProfile;
import static gmi.workouts.utils.EC2Helper.createEC2;
import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachRouteTableToSubnet;
import static gmi.workouts.utils.SecurityGroupHelper.DefaultPort.SSH;
import static gmi.workouts.utils.SecurityGroupHelper.createSecurityGroup;
import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class DnsStack108 extends Stack {
    public DnsStack108(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


        CfnVPC vpc1 = CfnVPC.Builder.create(this, "net-108-vpc-1")
                .cidrBlock("10.100.0.0/16")
                .enableDnsHostnames(true)
                .enableDnsSupport(true)
                .tags(createCommonTags("net-108-vpc-1"))
                .build();

        CfnVPC vpc2 = CfnVPC.Builder.create(this, "net-108-vpc-2")
                .cidrBlock("10.200.0.0/16")
                .enableDnsHostnames(false) // DISABLE DNS
                .enableDnsSupport(false) // DISABLE DNS
                .tags(createCommonTags("net-108-vpc-2"))
                .build();


        List<String> availabilityZones = Stack.of(this).getAvailabilityZones(); // easy way to get all AZ identifiers of the current region
        String oneAZ = availabilityZones.get(0);

        CfnSubnet subnet1 = CfnSubnet.Builder.create(this, "net-108-subnet-1")
                .cidrBlock("10.100.0.0/24")
                .vpcId(vpc1.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-108-subnet-1")).build();

        CfnSubnet subnet2 = CfnSubnet.Builder.create(this, "net-108-subnet-2")
                .cidrBlock("10.200.0.0/24")
                .vpcId(vpc2.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-108-subnet-2")).build();

        CfnInternetGateway internetGateway = createAndAttachInternetGateway(this, vpc1, "net-108-igw");
        createAndAttachRouteTableToSubnet(this, vpc1, subnet1, internetGateway, "net-108-rt-1");

        CfnSecurityGroup sg1 = createSecurityGroup(this, vpc1, "net-108-sg-1", SSH);
        CfnSecurityGroup sg2 = createSecurityGroup(this, vpc2, "net-108-sg-2");


        CfnInstanceProfile commonEC2InstanceProfile = createCommonEC2InstanceProfile(this);
        createEC2InVPC(subnet1, sg1, "net-108-ec2-1", commonEC2InstanceProfile);
        createEC2InVPC(subnet2, sg2, "net-108-ec2-2", commonEC2InstanceProfile);
    }


    private void createEC2InVPC(CfnSubnet subnet, CfnSecurityGroup securityGroup, String name, CfnInstanceProfile instanceProfile) {
        createEC2(this, subnet, securityGroup, name, true, instanceProfile);
    }
}

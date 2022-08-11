package gmi.workouts.networking.workout102;

import gmi.workouts.networking.workout101.VpcStack101;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.CfnTag;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.amazon.awscdk.services.ec2.CfnVPC;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.List;

import static gmi.workouts.CdkApp.PURPOSE;

public class BasicSubnetsStack102 extends Stack {

    private final CfnSubnet subnet2;

    public BasicSubnetsStack102(final Construct scope, final String id, final StackProps props, VpcStack101 vpcStack101) {
        super(scope, id, props);
        addDependency(vpcStack101);

        List<String> availabilityZones = Stack.of(this).getAvailabilityZones();
        String oneAZ = availabilityZones.get(0);
        String anotherAZ = availabilityZones.get(1);

        CfnVPC vpc1 = vpcStack101.getVpc1();
        // Create 4 subnets (in two AZ)
        CfnSubnet.Builder.create(this, "net-102-subnet-1")
                .cidrBlock("10.1.0.0/24")
                .vpcId(vpc1.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-102-subnet-1")).build();

        subnet2 = CfnSubnet.Builder.create(this, "net-102-subnet-2")
                .cidrBlock("10.1.1.0/24")
                .vpcId(vpc1.getAttrVpcId())
                .availabilityZone(anotherAZ)
                .tags(createCommonTags("net-102-subnet-2")).build();

        CfnSubnet.Builder.create(this, "net-102-subnet-3")
                .cidrBlock("10.1.224.0/20")
                .vpcId(vpc1.getAttrVpcId())
                .availabilityZone(oneAZ)
                .tags(createCommonTags("net-102-subnet-3")).build();

        CfnSubnet.Builder.create(this, "net-102-subnet-4")
                .cidrBlock("10.1.240.0/20")
                .vpcId(vpc1.getAttrVpcId())
                .availabilityZone(anotherAZ)
                .tags(createCommonTags("net-102-subnet-4")).build();

    }

    public CfnSubnet getSubnet2() {
        return subnet2;
    }

    @NotNull
    private static List<CfnTag> createCommonTags(String value) {
        return Arrays.asList(
                CfnTag.builder().key("Purpose").value(PURPOSE).build(),
                CfnTag.builder().key("Name").value(value).build()
        );
    }
}
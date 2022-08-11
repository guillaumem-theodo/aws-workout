package gmi.workouts.networking.workout101;

import software.amazon.awscdk.CfnTag;
import software.amazon.awscdk.services.ec2.CfnVPC;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

import static gmi.workouts.CdkApp.PURPOSE;

public class VpcStack101 extends Stack {

    private final CfnVPC vpc1;

    public VpcStack101(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        vpc1 = CfnVPC.Builder.create(this, "net-101-vpc")
                .cidrBlock("10.1.0.0/16")
                .tags(Arrays.asList(
                        CfnTag.builder().key("Purpose").value(PURPOSE).build(),
                        CfnTag.builder().key("Name").value("net-101-vpc").build(),
                        CfnTag.builder().key("Description").value("A Sample VPC with a CIDR of 10.1.0.0/16 in " + props.getEnv().getRegion()).build())
                ).build();

    }

    public CfnVPC getVpc1() {
        return vpc1;
    }
}

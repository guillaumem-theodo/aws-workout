package gmi.workouts.networking.workout101;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnVPC;
import software.constructs.Construct;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class VpcStack101 extends Stack {

    private final CfnVPC vpc1;

    public VpcStack101(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        vpc1 = CfnVPC.Builder.create(this, "net-101-vpc")
                .cidrBlock("10.1.0.0/16")
                .tags(createCommonTags("net-101-vpc"))
                .build();

    }

    public CfnVPC getVpc1() {
        return vpc1;
    }
}

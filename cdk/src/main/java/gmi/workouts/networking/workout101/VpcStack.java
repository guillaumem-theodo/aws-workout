package gmi.workouts.networking.workout101;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnVPC;
import software.constructs.Construct;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class VpcStack extends Stack {

    private final CfnVPC vpc;

    public VpcStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        vpc = CfnVPC.Builder.create(this, "net-101-vpc")
                .cidrBlock("10.1.0.0/16")
                .tags(createCommonTags("net-101-vpc"))
                .build();

    }

    public CfnVPC getVpc() {
        return vpc;
    }
}

package gmi.workouts.networking.workout101;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnVPC;
import software.constructs.Construct;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class VpcStack101 extends Stack {

    private final CfnVPC vpc;

/*  ######################################################################################
    ## 101 - Basic VPC
    ## Let's create a VPC !!!
    ## A VPC is a private cloud.
    ## - A set private IP (IPv4) addresses that will be available for your systems
    ## - CIDR specified at VPC creation defines the template of your private IP addresses
    ## - CIDR can rely on RFC 1918
    ## - A VPC is in ONE AWS Region (e.g. eu-west-2). Region is selected with the CDK environment
    ######################################################################################
*/
    public VpcStack101(final Construct scope, final String id, final StackProps props) {
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

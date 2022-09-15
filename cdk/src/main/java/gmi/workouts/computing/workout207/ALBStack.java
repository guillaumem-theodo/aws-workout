package gmi.workouts.computing.workout207;

import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.amazon.awscdk.services.elasticloadbalancingv2.CfnLoadBalancer;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;

public class ALBStack extends NestedStack {
    private final CfnLoadBalancer cfnLoadBalancer;


    public ALBStack(final Construct scope, final String id,
                    final VpcStack vpcStack,
                    final BasicSubnetsStack subnetsStack,
                    final ECSNetworkStack ecsNetworkStack) {
        super(scope, id);
        addDependency(vpcStack);
        addDependency(subnetsStack);
        addDependency(ecsNetworkStack);

        CfnSubnet firstSubnetForALB = subnetsStack.getSubnet1();
        CfnSubnet secondSubnetForALB = subnetsStack.getSubnet2();

        cfnLoadBalancer = CfnLoadBalancer.Builder.create(this, "cpu-207-alb")
                .name("cpu-207-alb")
                .type("application")
                .subnets(Arrays.asList(firstSubnetForALB.getAttrSubnetId(), secondSubnetForALB.getAttrSubnetId()))
                .securityGroups(Collections.singletonList(ecsNetworkStack.getAlbSecurityGroup().getAttrGroupId()))
                .build();
    }

    public CfnLoadBalancer getCfnLoadBalancer() {
        return cfnLoadBalancer;
    }
}

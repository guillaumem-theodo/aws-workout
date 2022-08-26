package gmi.workouts.computing.workout207;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.utils.SecurityGroupHelper;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.services.ec2.CfnInternetGateway;
import software.amazon.awscdk.services.ec2.CfnSecurityGroup;
import software.amazon.awscdk.services.ec2.CfnSecurityGroupIngress;
import software.amazon.awscdk.services.ec2.CfnSubnet;
import software.amazon.awscdk.services.elasticloadbalancingv2.CfnLoadBalancer;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;

import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachInternetGateway;
import static gmi.workouts.utils.InternetGatewayHelper.createAndAttachRouteTableToSubnets;
import static gmi.workouts.utils.MyIpHelper.getMyIPAddressCIDR;
import static gmi.workouts.utils.NatGatewayHelper.createAndAttachNatGateway;
import static gmi.workouts.utils.SecurityGroupHelper.DefaultPort.HTTP;

public class ALBStack207 extends NestedStack {
    private final CfnLoadBalancer cfnLoadBalancer;


    public ALBStack207(final Construct scope, final String id,
                       VpcStack101 vpcStack101,
                       BasicSubnetsStack102 basicSubnetsStack102,
                       ECSNetworkStack207 albNetworkStack207) {
        super(scope, id);
        addDependency(vpcStack101);
        addDependency(basicSubnetsStack102);
        addDependency(albNetworkStack207);

        cfnLoadBalancer = CfnLoadBalancer.Builder.create(this, "cpu-207-alb")
                .name("cpu-207-alb")
                .type("application")
                .subnets(Arrays.asList(basicSubnetsStack102.getSubnet1().getAttrSubnetId(),
                        basicSubnetsStack102.getSubnet2().getAttrSubnetId()))
                .securityGroups(Collections.singletonList(albNetworkStack207.getAlbSecurityGroup().getAttrGroupId()))
                .build();
    }

    public CfnLoadBalancer getCfnLoadBalancer() {
        return cfnLoadBalancer;
    }
}

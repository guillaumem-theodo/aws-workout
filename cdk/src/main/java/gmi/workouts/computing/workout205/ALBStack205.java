package gmi.workouts.computing.workout205;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.services.ec2.CfnInstance;
import software.amazon.awscdk.services.ec2.CfnSecurityGroup;
import software.amazon.awscdk.services.elasticloadbalancingv2.CfnListener;
import software.amazon.awscdk.services.elasticloadbalancingv2.CfnLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.CfnTargetGroup;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;

import static gmi.workouts.utils.EC2Helper.Ip.WITHOUT_PUBLIC_IP;
import static gmi.workouts.utils.EC2Helper.Ip.WITH_PUBLIC_IP;
import static gmi.workouts.utils.EC2Helper.createEC2;
import static gmi.workouts.utils.EC2Helper.encodeUserData;

public class ALBStack205 extends NestedStack {
    private static final String USER_DATA_SCRIPT =
            "#!/bin/bash\n" +
                    "sudo su\n" +
                    "yum update -y\n" +
                    "yum install -y httpd\n" +
                    "systemctl start httpd\n" +
                    "systemctl enable httpd\n" +
                    "echo \"Hello World from $(hostname -f)\" > /var/www/html/index.html\n";
    private CfnInstance worker1;
    private CfnInstance worker2;
    private CfnInstance worker3;

    public ALBStack205(final Construct scope, final String id,
                       VpcStack101 vpcStack101,
                       BasicSubnetsStack102 basicSubnetsStack102,
                       ALBNetworkStack205 albNetworkStack205) {
        super(scope, id);
        addDependency(vpcStack101);
        addDependency(basicSubnetsStack102);
        addDependency(albNetworkStack205);

        createWorkers(basicSubnetsStack102, albNetworkStack205);

        createBastion(basicSubnetsStack102, albNetworkStack205);

        createAlb(vpcStack101, basicSubnetsStack102, albNetworkStack205);
    }

    private void createBastion(BasicSubnetsStack102 subnets, ALBNetworkStack205 albNetworkStack205) {
        createEC2(this, "cpu-205-ec2-bastion-1", subnets.getSubnet2(),
                albNetworkStack205.getBastionSecurityGroup(), WITH_PUBLIC_IP);
    }

    private void createWorkers(BasicSubnetsStack102 subnets, ALBNetworkStack205 albNetworkStack205) {
        CfnSecurityGroup workerSecurityGroup = albNetworkStack205.getWorkerSecurityGroup();

        worker1 = createEC2(this, "cpu-205-ec2-worker-1", subnets.getSubnet3(),
                workerSecurityGroup, WITHOUT_PUBLIC_IP,
                builder -> builder.userData(encodeUserData(USER_DATA_SCRIPT)));

        worker2 = createEC2(this, "cpu-205-ec2-worker-2", subnets.getSubnet3(),
                workerSecurityGroup, WITHOUT_PUBLIC_IP,
                builder -> builder.userData(encodeUserData(USER_DATA_SCRIPT)));

        worker3 = createEC2(this, "cpu-205-ec2-worker-3", subnets.getSubnet4(),
                workerSecurityGroup, WITHOUT_PUBLIC_IP,
                builder -> builder.userData(encodeUserData(USER_DATA_SCRIPT)));
    }

    private void createAlb(VpcStack101 vpcStack101, BasicSubnetsStack102 subnets, ALBNetworkStack205 albNetwork) {

        CfnLoadBalancer cfnLoadBalancer = CfnLoadBalancer.Builder.create(this, "cpu-205-alb")
                .name("cpu-205-alb")
                .type("application")
                .subnets(Arrays.asList(subnets.getSubnet1().getAttrSubnetId(), subnets.getSubnet2().getAttrSubnetId()))
                .securityGroups(Collections.singletonList(albNetwork.getAlbSecurityGroup().getAttrGroupId()))
                .build();


        CfnTargetGroup targetGroup = CfnTargetGroup.Builder.create(this, "cpu-205-alb-target-group")
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
                .name("cpu-205-alb-target-group")
                .port(80)
                .protocol("HTTP")
                .targetType("instance")
                .targets(Arrays.asList(
                        CfnTargetGroup.TargetDescriptionProperty.builder().port(80).id(worker1.getRef()).build(),
                        CfnTargetGroup.TargetDescriptionProperty.builder().port(80).id(worker2.getRef()).build(),
                        CfnTargetGroup.TargetDescriptionProperty.builder().port(80).id(worker3.getRef()).build()
                ))
                .build();

        CfnListener.ActionProperty forwardAction = CfnListener.ActionProperty.builder()
                .type("forward")
                .forwardConfig(CfnListener.ForwardConfigProperty.builder()
                        .targetGroups(Collections.singletonList(
                                CfnListener.TargetGroupTupleProperty.builder().weight(100).targetGroupArn(targetGroup.getRef()).build()
                        ))
                        .build())
                .build();

        CfnListener cfnListener = CfnListener.Builder.create(this, "cpu-205-alb-listener")
                .port(80)
                .protocol("HTTP")
                .loadBalancerArn(cfnLoadBalancer.getRef())
                .defaultActions(Collections.singletonList(forwardAction))
                .build();


    }
}

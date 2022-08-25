package gmi.workouts.computing.workout206;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.autoscaling.CfnAutoScalingGroup;
import software.amazon.awscdk.services.ec2.CfnLaunchTemplate;
import software.amazon.awscdk.services.ec2.IMachineImage;
import software.amazon.awscdk.services.ec2.MachineImage;
import software.amazon.awscdk.services.elasticloadbalancingv2.CfnListener;
import software.amazon.awscdk.services.elasticloadbalancingv2.CfnLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.CfnTargetGroup;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gmi.workouts.utils.EC2Helper.*;
import static gmi.workouts.utils.EC2Helper.Ip.WITH_PUBLIC_IP;

public class ASGStack206 extends NestedStack {
    private static final String USER_DATA_SCRIPT =
            "#!/bin/bash\n" +
                    "sudo su\n" +
                    "yum update -y\n" +
                    "yum install -y httpd\n" +
                    "systemctl start httpd\n" +
                    "systemctl enable httpd\n" +
                    "echo \"Hello World from $(hostname -f)\" > /var/www/html/index.html\n";


    public ASGStack206(final Construct scope, final String id,
                       VpcStack101 vpcStack101,
                       BasicSubnetsStack102 basicSubnetsStack102,
                       ASGNetworkStack206 albNetworkStack206) {
        super(scope, id);
        addDependency(vpcStack101);
        addDependency(basicSubnetsStack102);
        addDependency(albNetworkStack206);

        createBastion(basicSubnetsStack102, albNetworkStack206);

        createAlb(vpcStack101, basicSubnetsStack102, albNetworkStack206);
    }




    private void createAlb(VpcStack101 vpcStack101, BasicSubnetsStack102 subnets, ASGNetworkStack206 albNetwork) {

        CfnLoadBalancer cfnLoadBalancer = CfnLoadBalancer.Builder.create(this, "cpu-206-alb")
                .name("cpu-206-alb")
                .type("application")
                .subnets(Arrays.asList(subnets.getSubnet1().getAttrSubnetId(), subnets.getSubnet2().getAttrSubnetId()))
                .securityGroups(Collections.singletonList(albNetwork.getAlbSecurityGroup().getAttrGroupId()))
                .build();


        CfnTargetGroup targetGroup = CfnTargetGroup.Builder.create(this, "cpu-206-alb-target-group")
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
                .name("cpu-206-alb-target-group")
                .port(80)
                .protocol("HTTP")
                .targetType("instance")
                .build();

        CfnAutoScalingGroup asg = createAsg(subnets, albNetwork, targetGroup);


        CfnListener.ActionProperty forwardAction = CfnListener.ActionProperty.builder()
                .type("forward")
                .forwardConfig(CfnListener.ForwardConfigProperty.builder()
                        .targetGroups(Collections.singletonList(
                                CfnListener.TargetGroupTupleProperty.builder().weight(100).targetGroupArn(targetGroup.getRef()).build()
                        ))
                        .build())
                .build();

        CfnListener cfnListener = CfnListener.Builder.create(this, "cpu-206-alb-listener")
                .port(80)
                .protocol("HTTP")
                .loadBalancerArn(cfnLoadBalancer.getRef())
                .defaultActions(Collections.singletonList(forwardAction))
                .build();


    }

    private CfnAutoScalingGroup createAsg(BasicSubnetsStack102 basicSubnetsStack102, ASGNetworkStack206 albNetworkStack206, CfnTargetGroup targetGroup) {
        IMachineImage latestAMI = MachineImage.fromSsmParameter(LINUX_LATEST_AMZN_2_AMI_HVM_X_86_64_GP_2, null);

        CfnLaunchTemplate launchTemplate = CfnLaunchTemplate.Builder.create(this, "cpu-206-launch-template")
                .launchTemplateData(CfnLaunchTemplate.LaunchTemplateDataProperty.builder()
                        .imageId(latestAMI.getImage(this).getImageId())
                        .keyName(AWS_WORKOUT_KEY)
                        .instanceType(INSTANCE_TYPE)
                        .networkInterfaces(
                                Collections.singletonList(
                                        CfnLaunchTemplate.NetworkInterfaceProperty.builder()
                                                .subnetId(basicSubnetsStack102.getSubnet3().getAttrSubnetId())
                                                .associatePublicIpAddress(false)
                                                .groups(Collections.singletonList(albNetworkStack206.getWorkerSecurityGroup().getAttrGroupId()))
                                                .deviceIndex(0).build()

                                ))
                        .userData(encodeUserData(USER_DATA_SCRIPT))
                        .build())
               .build();

        List<String> availabilityZones = Stack.of(this).getAvailabilityZones(); // easy way to get all AZ identifiers of the current region
        return CfnAutoScalingGroup.Builder.create(this, "cpu-206-asg")
                .autoScalingGroupName("cpu-206-asg")
                .availabilityZones(Collections.singletonList(availabilityZones.get(0)))
                .desiredCapacity("3")
                .maxSize("4")
                .minSize("2")
                .targetGroupArns(Collections.singletonList(targetGroup.getRef()))
                .launchTemplate(CfnAutoScalingGroup.LaunchTemplateSpecificationProperty.builder()
                        .launchTemplateId(launchTemplate.getRef())
                        .version(launchTemplate.getAttrLatestVersionNumber())
                        .build())
                .build();
    }
    
    private void createBastion(BasicSubnetsStack102 subnets, ASGNetworkStack206 albNetworkStack206) {
        createEC2(this, "cpu-206-ec2-bastion-1", subnets.getSubnet2(),
                albNetworkStack206.getBastionSecurityGroup(), WITH_PUBLIC_IP);
    }
}

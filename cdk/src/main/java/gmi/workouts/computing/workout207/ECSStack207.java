package gmi.workouts.computing.workout207;

import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.services.ecs.CfnCluster;
import software.amazon.awscdk.services.ecs.CfnService;
import software.amazon.awscdk.services.ecs.CfnTaskDefinition;
import software.amazon.awscdk.services.elasticloadbalancingv2.CfnListener;
import software.amazon.awscdk.services.elasticloadbalancingv2.CfnTargetGroup;
import software.constructs.Construct;

import java.util.Collections;

public class ECSStack207 extends NestedStack {
    public ECSStack207(final Construct scope, final String id,
                       VpcStack101 vpcStack101,
                       BasicSubnetsStack102 basicSubnetsStack102,
                       ECSNetworkStack207 albNetworkStack207,
                       ALBStack207 albStack207) {
        super(scope, id);
        addDependency(vpcStack101);
        addDependency(basicSubnetsStack102);
        addDependency(albNetworkStack207);
        addDependency(albStack207);

        createEcs(vpcStack101, basicSubnetsStack102, albNetworkStack207, albStack207);
    }

    private void createEcs(VpcStack101 vpcStack101, BasicSubnetsStack102 subnets, ECSNetworkStack207 albNetwork, ALBStack207 albStack207) {

        CfnTargetGroup targetGroup = CfnTargetGroup.Builder.create(this, "cpu-207-alb-target-group")
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
                .name("cpu-207-alb-target-group")
                .port(80)
                .protocol("HTTP")
                .targetType("ip") // Needed for FARGATE awsvpc network mode
                .build();

        CfnCluster ecs = createEcs(subnets, albNetwork, targetGroup);

        CfnListener.ActionProperty forwardAction = CfnListener.ActionProperty.builder()
                .type("forward")
                .forwardConfig(CfnListener.ForwardConfigProperty.builder()
                        .targetGroups(Collections.singletonList(
                                CfnListener.TargetGroupTupleProperty.builder().weight(100).targetGroupArn(targetGroup.getRef()).build()
                        ))
                        .build())
                .build();

        CfnListener cfnListener = CfnListener.Builder.create(this, "cpu-207-alb-listener")
                .port(80)
                .protocol("HTTP")
                .loadBalancerArn(albStack207.getCfnLoadBalancer().getRef())
                .defaultActions(Collections.singletonList(forwardAction))
                .build();


    }

    private CfnCluster createEcs(BasicSubnetsStack102 basicSubnetsStack102, ECSNetworkStack207 albNetworkStack207, CfnTargetGroup targetGroup) {

        CfnCluster cfnCluster = CfnCluster.Builder.create(this, "cpu-207-ecs")
                .clusterName("cpu-207-ecs")
                .capacityProviders(Collections.singletonList("FARGATE"))
                .defaultCapacityProviderStrategy(Collections.singletonList(
                        CfnCluster.CapacityProviderStrategyItemProperty.builder()
                                .weight(100)
                                .capacityProvider("FARGATE")
                                .base(1)
                                .build()))
                .build();

        CfnTaskDefinition cfnTaskDefinition = CfnTaskDefinition.Builder.create(this, "cpu-207-ecs-task-definition")
                .family("pu-207-ecs-task-definition")
                .requiresCompatibilities(Collections.singletonList("FARGATE"))
                .networkMode("awsvpc")
                .cpu("256")
                .memory("512")
                .containerDefinitions(Collections.singletonList(CfnTaskDefinition.ContainerDefinitionProperty.builder()
                        .name("cpu-207-container-httpd")
                        .image("httpd:2.4") // Name of the image in DockerHub
                        .essential(true)
                        .portMappings(Collections.singletonList(
                                CfnTaskDefinition.PortMappingProperty.builder()
                                        .containerPort(80)
                                        .hostPort(80)
                                        .build()
                        ))
                        .build()
                ))
                .build();

        CfnService.Builder.create(this, "cpu-207-ecs-service")
                .serviceName("cpu-207-ecs-service")
                .cluster(cfnCluster.getAttrArn())
                .taskDefinition(cfnTaskDefinition.getRef())
                .desiredCount(3)
                .launchType("FARGATE")
                .networkConfiguration(CfnService.NetworkConfigurationProperty.builder()
                        .awsvpcConfiguration(CfnService.AwsVpcConfigurationProperty.builder()
                                .subnets(Collections.singletonList(basicSubnetsStack102.getSubnet3().getAttrSubnetId()))
                                .securityGroups(Collections.singletonList(albNetworkStack207.getWorkerSecurityGroup().getAttrGroupId()))
                                .build())
                        .build())
                .loadBalancers(Collections.singletonList(CfnService.LoadBalancerProperty.builder()
                                .containerPort(80)
                                .containerName("cpu-207-container-httpd")
                                .targetGroupArn(targetGroup.getRef())
                        .build()))
                .build();


        return cfnCluster;
    }

}

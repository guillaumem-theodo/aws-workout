package gmi.workouts.networking.workout107nat;

import gmi.workouts.common.S3ForTestsStack;
import gmi.workouts.networking.workout101.VpcStack101;
import gmi.workouts.networking.workout102.BasicSubnetsStack102;
import gmi.workouts.networking.workout105.BastionStack105;
import gmi.workouts.networking.workout106.NatGatewayStack106;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnRouteTable;
import software.amazon.awscdk.services.ec2.CfnVPCEndpoint;
import software.constructs.Construct;

import java.util.Collections;

public class VpcEndpointWithNatStack107 extends Stack {

    public VpcEndpointWithNatStack107(final Construct scope, final String id, final StackProps props,
                                      VpcStack101 vpcStack101,
                                      BasicSubnetsStack102 subnetsStack102,
                                      BastionStack105 bastionStack105,
                                      NatGatewayStack106 natGatewayStack106,
                                      S3ForTestsStack s3ForTestsInFirstRegionStack,
                                      S3ForTestsStack s3ForTestsInSecondRegionStack) {
        super(scope, id, props);
        addDependency(vpcStack101);
        addDependency(subnetsStack102);
        addDependency(bastionStack105);
        addDependency(natGatewayStack106);

        addDependency(s3ForTestsInFirstRegionStack);
        addDependency(s3ForTestsInSecondRegionStack);

        CfnRouteTable privateRouteTable = bastionStack105.getPrivateRouteTable();

        String tutorialRegion = System.getenv("TUTORIAL_REGION");
        CfnVPCEndpoint.Builder.create(this, "vpc-endpoint-1-107")
                .vpcId(vpcStack101.getVpc().getAttrVpcId())
                .routeTableIds(Collections.singletonList(privateRouteTable.getAttrRouteTableId()))
                .serviceName("com.amazonaws."+tutorialRegion+".s3")
                .build();
    }
}

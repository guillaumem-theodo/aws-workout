package gmi.workouts.networking.workout107nat;

import gmi.workouts.common.S3ForTestsStack;
import gmi.workouts.networking.workout101.VpcStack;
import gmi.workouts.networking.workout102.BasicSubnetsStack;
import gmi.workouts.networking.workout105.BastionStack;
import gmi.workouts.networking.workout106.NatGatewayStack;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.CfnRouteTable;
import software.amazon.awscdk.services.ec2.CfnVPCEndpoint;
import software.constructs.Construct;

import java.util.Collections;

import static gmi.workouts.utils.TagsHelper.addCommonTags;

public class VpcEndpointWithNatStack extends Stack {

    public VpcEndpointWithNatStack(final Construct scope, final String id, final StackProps props,
                                   final VpcStack vpcStack,
                                   final BasicSubnetsStack subnetsStack102,
                                   final BastionStack bastionStack,
                                   final NatGatewayStack natGatewayStack,
                                   final S3ForTestsStack s3InFirstRegionStack,
                                   final S3ForTestsStack s3InSecondRegionStack) {
        super(scope, id, props);
        addDependency(vpcStack);
        addDependency(subnetsStack102);
        addDependency(bastionStack);
        addDependency(natGatewayStack); // ADD a NAT Gateway and a route to internet

        addDependency(s3InFirstRegionStack);
        addDependency(s3InSecondRegionStack);

        CfnRouteTable privateRouteTable = bastionStack.getPrivateRouteTable();

        String tutorialRegion = Stack.of(this).getRegion();
        CfnVPCEndpoint vpcEndpoint = CfnVPCEndpoint.Builder.create(this, "net-107-vpc-endpoint-2")
                .vpcId(vpcStack.getVpc().getAttrVpcId())
                .routeTableIds(Collections.singletonList(privateRouteTable.getAttrRouteTableId()))
                .serviceName("com.amazonaws." + tutorialRegion + ".s3")
                .build();

        addCommonTags(vpcEndpoint,"net-107-vpc-endpoint-2" );
    }
}

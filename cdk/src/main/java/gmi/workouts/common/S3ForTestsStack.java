package gmi.workouts.common;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.deployment.BucketDeployment;
import software.amazon.awscdk.services.s3.deployment.Source;
import software.constructs.Construct;

import java.util.Collections;

public class S3ForTestsStack extends Stack {

    public S3ForTestsStack(final Construct scope, String id, StackProps props, String suffixName) {
        super(scope, id, props);

        createBucketInFirstRegionForTests(this, suffixName);
    }

    private static void createBucketInFirstRegionForTests(final Construct scope, String suffixName) {
        String tutorialUniqueKey = System.getenv("TUTORIAL_UNIQUE_KEY");

        Bucket bucket = Bucket.Builder.create(scope, "common-" + suffixName)
                .bucketName(tutorialUniqueKey + "-" + suffixName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .autoDeleteObjects(true)
                .build();

        BucketDeployment.Builder.create(scope, "common-"+suffixName+"-deployment")
                .destinationBucket(bucket)
                .sources(Collections.singletonList(Source.asset("./fixtures"))) // Fixtures are found at CDK root level
                .build();
    }
}

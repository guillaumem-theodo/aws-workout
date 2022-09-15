package gmi.workouts.utils;

import software.amazon.awscdk.CfnTag;
import software.amazon.awscdk.Tags;
import software.constructs.Construct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagsHelper {
    public static List<CfnTag> createCommonTags(String name, CfnTag... tags) {
        List<CfnTag> cfnTags = new ArrayList<>();
        cfnTags.add(CfnTag.builder().key("Name").value(name).build());
        cfnTags.addAll(Arrays.asList(tags));
        return cfnTags;
    }

    public static void addCommonTags(Construct construct, String name) {
        Tags.of(construct).add("Name", name);
    }
}

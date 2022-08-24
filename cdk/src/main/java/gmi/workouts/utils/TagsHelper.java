package gmi.workouts.utils;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.CfnTag;
import software.amazon.awscdk.Tags;
import software.constructs.Construct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gmi.workouts.CdkApp.PURPOSE;

public class TagsHelper {
    @NotNull
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

package gmi.workouts.utils;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.CfnTag;

import java.util.Arrays;
import java.util.List;

import static gmi.workouts.CdkApp.PURPOSE;

public class TagsHelper {
    @NotNull
    public static List<CfnTag> createCommonTags(String name, CfnTag... tags) {
        List<CfnTag> cfnTags = Arrays.asList(
                CfnTag.builder().key("Purpose").value(PURPOSE).build(),
                CfnTag.builder().key("Name").value(name).build()
        );
        cfnTags.addAll(Arrays.asList(tags));
        return cfnTags;
    }
}

package top.kwseeker.core.guava;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Assert;

import java.util.List;

public class StringCatcher {
    private List<String> events = Lists.newArrayList();

    @Subscribe
    public void hereHaveAString(@Nullable String string) {
        events.add(string);
    }

    //没有被@Subscribe注解的方法不会被回调
    public void methodWithoutAnnotation(@Nullable String string) {
        Assert.fail("Event bus must not call methods without @Subscribe!");
    }

    public List<String> getEvents() {
        return events;
    }
}

package top.kwseeker.core.guava;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import junit.framework.TestCase;

import java.util.List;

public class ReentrantEventsTest extends TestCase {

    static final String FIRST = "one";
    static final Double SECOND = 2.0d;

    final EventBus bus = new EventBus();

    //测试事件重入
    public void testNoReentrantEvents() {
        ReentrantEventsHater hater = new ReentrantEventsHater();
        bus.register(hater);
        bus.post(FIRST);

        assertEquals(
                "ReentrantEventHater expected 2 events",
                Lists.<Object>newArrayList(FIRST, SECOND),
                hater.eventsReceived);
    }

    public class ReentrantEventsHater {
        boolean ready = true;
        List<Object> eventsReceived = Lists.newArrayList();

        @Subscribe
        public void listenForStrings(String event) {
            eventsReceived.add(event);
            ready = false;
            try {
                bus.post(SECOND);
            } finally {
                ready = true;
            }
        }

        @Subscribe
        public void listenForDoubles(Double event) {
            assertTrue("I received an event when I wasn't ready!", ready);
            eventsReceived.add(event);
        }
    }

    //测试有先后顺序依赖的事件
    public void testEventOrderingIsPredictable() {
        EventProcessor processor = new EventProcessor();
        bus.register(processor);

        EventRecorder recorder = new EventRecorder();
        bus.register(recorder);

        bus.post(FIRST);

        assertEquals(
                "EventRecorder expected events in order",
                Lists.<Object>newArrayList(FIRST, SECOND),
                recorder.eventsReceived);
    }

    public class EventProcessor {
        @Subscribe
        public void listenForStrings(String event) {
            bus.post(SECOND);
        }
    }

    public class EventRecorder {
        List<Object> eventsReceived = Lists.newArrayList();

        @Subscribe
        public void listenForEverything(Object event) {
            eventsReceived.add(event);
        }
    }
}

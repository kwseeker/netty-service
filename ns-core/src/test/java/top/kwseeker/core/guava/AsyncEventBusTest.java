package top.kwseeker.core.guava;

import com.google.common.collect.Lists;
import com.google.common.eventbus.AsyncEventBus;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * AsyncEventBus事件消费只能通过Executor
 */
public class AsyncEventBusTest {

    private static final String EVENT = "Hello";
    /** The executor we use to fake asynchronicity. */
    private FakeExecutor executor;
    private AsyncEventBus bus;

    @Before
    public void init() throws Exception {
        executor = new FakeExecutor();
        //1）创建异步事件总线及处理事件的executor
        bus = new AsyncEventBus(executor);
    }

    @Test
    public void testBasicDistribution() {
        //2）创建订阅者
        StringCatcher catcher = new StringCatcher();
        //3）注册订阅者
        bus.register(catcher);

        // We post the event, but our Executor will not deliver it until instructed.
        //4）发布事件
        bus.post(EVENT);

        List<String> events = catcher.getEvents();
        assertTrue("No events should be delivered synchronously.", events.isEmpty());

        // Now we find the task in our Executor and explicitly activate it.
        List<Runnable> tasks = executor.getTasks();
        assertEquals("One event dispatch task should be queued.", 1, tasks.size());
        tasks.get(0).run();

        assertEquals("One event should be delivered.", 1, events.size());
        assertEquals("Correct string should be delivered.", EVENT, events.get(0));
    }

    /**
     * An {@link Executor} wanna-be that simply records the tasks it's given. Arguably the Worst
     * Executor Ever.
     *
     * @author cbiffle
     */
    public static class FakeExecutor implements Executor {
        List<Runnable> tasks = Lists.newArrayList();

        @Override
        public void execute(Runnable task) {
            tasks.add(task);
        }

        public List<Runnable> getTasks() {
            return tasks;
        }
    }
}

package top.kwseeker.core.guava;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.Before;
import org.junit.Test;

/**
 * Guava消息总线，发布订阅功能测试demo
 */
public class EventBusSimplestTest {

    private EventBus eventBus;

    @Before
    public void init() {
        //1）创建事件总线
        eventBus = new EventBus("Simplest EventBus");
        //2）创建事件订阅者
        EventSubscriber subscriber = new EventSubscriber();
        //3）将事件订阅者注册到事件总线
        eventBus.register(subscriber);
    }

    private void postStrings(int reps) {
        for (int i = 0; i < reps; i++) {
            //4）发布事件，事件可以是任何类型（？extends Object）
            eventBus.post("Hello " + String.valueOf(i));
        }
    }

    @Test
    public void testPubSub() {
        postStrings(3);
    }

    public static class EventSubscriber {
        //2.1）事件发布后触发的回调函数
        @Subscribe
        public void handleChange(String changeEvent) {
            System.out.println("event: " + changeEvent);
        }
    }
}

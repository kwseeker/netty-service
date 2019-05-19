package top.kwseeker.core;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.event.Event;
import top.kwseeker.util.thread.ThreadPoolUtil;

import java.util.concurrent.Executor;

/**
 * 创建Guava异步事件总线单例实例
 * 处理连接广播事件（如：需要定期向所有连接发送心跳确保连接正常）
 */
public class EventBus {

    private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);

    public static final EventBus INSTANCE = new EventBus();
    private final com.google.common.eventbus.EventBus eventBus;

    public EventBus() {
        Executor executor = ThreadPoolUtil.getThreadPoolManager().getThreadExecutor(
                "event-bus-pool", 4, 4);
        eventBus = new AsyncEventBus(executor, new SubscriberExceptionHandler() {
            @Override
            public void handleException(Throwable exception, SubscriberExceptionContext context) {
                LOG.error("event bus subscriber ex", exception);
            }
        });
    }

    public void post(Event event) {
        eventBus.post(event);
    }

    public void register(Object bean) {
        eventBus.register(bean);
    }

    public void unregister(Object bean) {
        eventBus.unregister(bean);
    }
}

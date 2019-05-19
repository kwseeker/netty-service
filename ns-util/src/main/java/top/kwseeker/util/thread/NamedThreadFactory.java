package top.kwseeker.util.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工厂
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNum = new AtomicInteger(1);
    private final AtomicInteger threadNum = new AtomicInteger(1);

    private final ThreadGroup group;
    //线程名称前缀
    private final String namePre;
    private final boolean isDaemon;

    public NamedThreadFactory(){
        this("pool");
    }

    public NamedThreadFactory(String prefix){
        this(prefix,true);
    }

    public NamedThreadFactory(String prefix, boolean daemon) {
        //TODO：获取SecurityManager的线程组的意义？
        SecurityManager manager = System.getSecurityManager();
        if(manager!=null){
            group = manager.getThreadGroup();
        }else{
            group = Thread.currentThread().getThreadGroup();
        }
        isDaemon = daemon;
        namePre = prefix+"-"+poolNum.getAndIncrement()+"-thread-";
    }

    /**
     * stackSize - 新线程的预期堆栈大小，为零时表示忽略该参数
     */
    @Override
    public Thread newThread(Runnable runnable) {
        Thread t = new Thread(group, runnable,namePre+threadNum.getAndIncrement(),0);
        //TODO:指定线程上下文类加载器实现成功加载SPI实现类,配合 Class.forName()
        t.setContextClassLoader(NamedThreadFactory.class.getClassLoader());
        t.setPriority(Thread.MAX_PRIORITY);
        t.setDaemon(isDaemon);
        return t;
    }


}

package top.kwseeker.util.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.util.Constants;
import top.kwseeker.util.JVMUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池管理器管理多个线程池
 * defaultPoolExecutor
 * poolCache
 */
public class ThreadPoolManager {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolManager.class);

    private static final long keepAliveTime = 300L;
    //有界队列满后无法插入被Reject的处理策略
    private final RejectedExecutionHandler handler = new IgnoreRunsPolicy();
    //默认线程池
    private final ThreadPoolExecutor defaultPoolExecutor;
    //线程池（多个）缓存到HashMap
    private final Map<String, ThreadPoolExecutor> poolCache = new HashMap<>();

    public ThreadPoolManager(int corePoolSize, int maxPoolSize, int queueSize) {
        final BlockingQueue<Runnable> workQueue = new SynchronousQueue<>(); //TODO
        final ThreadFactory threadFactory = new NamedThreadFactory(ThreadNameSpace.NETTY_WORKER);
        defaultPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                handler);
    }

    //为每一个服务分配一个线程池
    public void allocThreadPool(final String serviceUniqueName, int corePoolSize, int maxPoolSize)
        throws Exception {
        //检查是否已经包含此服务的线程池
        if(poolCache.containsKey(serviceUniqueName)) {
            throw new Exception(MessageFormat.format(
                    "[ThreadPool Manager] Duplicated thread pool allocation request for service [{0}].",
                    new Object[] { serviceUniqueName }));
        }
        if(defaultPoolExecutor == null || defaultPoolExecutor.isShutdown()) {
            throw new Exception(MessageFormat.format(
                    "[ThreadPool Manager] Can not allocate thread pool for service [{0}].",
                    new Object[] { serviceUniqueName }));
        }

        int balance = defaultPoolExecutor.getMaximumPoolSize();
        if(balance < maxPoolSize) {
            throw new Exception(MessageFormat.format(
                    "[ThreadPool Manager] Thread pool allocated failed for service [{0}]: balance [{1}] require [{2}].",
                    new Object[] { serviceUniqueName, balance, maxPoolSize }));
        }

        ThreadFactory threadFactory = new NamedThreadFactory(ThreadNameSpace.getUniqueName(serviceUniqueName));
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactory, handler);
        poolCache.put(serviceUniqueName, executor);

        int newBalance = balance - maxPoolSize;
        if(newBalance == 0) {
            defaultPoolExecutor.shutdown();
        } else {
            if(newBalance < defaultPoolExecutor.getCorePoolSize()) {
                defaultPoolExecutor.setCorePoolSize(newBalance);
            }
            defaultPoolExecutor.setMaximumPoolSize(newBalance);
        }
    }

    //
    public Executor getThreadExecutor(String serviceUniqueName, int corePoolSize, int maximumPoolSize) {
        if (!poolCache.isEmpty()) {
            ThreadPoolExecutor executor = poolCache.get(serviceUniqueName);
            if (executor != null) {
                return executor;
            }else{
                try{
                    allocThreadPool(serviceUniqueName, corePoolSize, maximumPoolSize);
                }catch(Exception e){
                    LOG.error("allocThreadPool exception",e);
                }
            }
            executor = poolCache.get(serviceUniqueName);
            if(executor!=null){
                return executor;
            }
        }else{
            try{
                allocThreadPool(serviceUniqueName, corePoolSize, maximumPoolSize);
            }catch(Exception e){
                LOG.error("allocThreadPool exception",e);
            }
            ThreadPoolExecutor executor = poolCache.get(serviceUniqueName);
            if (executor != null) {
                return executor;
            }
        }
        return defaultPoolExecutor;
    }

    public Executor getThreadExecutor(String serviceUniqueName) {
        if (!poolCache.isEmpty()) {
            ThreadPoolExecutor executor = poolCache.get(serviceUniqueName);
            if (executor != null) {
                return executor;
            }
        }
        return defaultPoolExecutor;
    }

    public void shutdown() {
        if (defaultPoolExecutor != null && !defaultPoolExecutor.isShutdown()) {
            defaultPoolExecutor.shutdown();
        }

        if (!poolCache.isEmpty()) {
            Iterator<ThreadPoolExecutor> ite = poolCache.values().iterator();
            while (ite.hasNext()) {
                ThreadPoolExecutor poolExecutor = ite.next();
                poolExecutor.shutdown();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("当前线程池分配策略：");
        Iterator<Map.Entry<String, ThreadPoolExecutor>> ite = poolCache.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<String, ThreadPoolExecutor> entry = ite.next();
            String serviceUniqName = entry.getKey();
            ThreadPoolExecutor executor = entry.getValue();
            sb.append("服务[" + serviceUniqName + "]核心线程数量：" + executor.getCorePoolSize() + " 最大线程数量："
                    + executor.getMaximumPoolSize() + " 活动线程数量：" + executor.getActiveCount());
        }

        if (!defaultPoolExecutor.isShutdown()) {
            sb.append("服务默认使用的核心线程数量：" + defaultPoolExecutor.getCorePoolSize() + " 最大线程数量： "
                    + defaultPoolExecutor.getMaximumPoolSize() + " 活动线程数量：" + defaultPoolExecutor.getActiveCount());
        }

        return sb.toString();
    }

    //自定义有界队列满之后的处理策略，这里打印JVM堆栈信息到文件，然后再抛出 RejectedExecutionException
    private static class IgnoreRunsPolicy implements RejectedExecutionHandler {
        private final static Logger LOG = LoggerFactory.getLogger(IgnoreRunsPolicy.class);
        private volatile boolean dump = false;

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            dumpJVMInfo();
            throw new RejectedExecutionException();
        }

        //创建一个单线程的线程池处理打印堆栈信息到jvmStack.log文件
        private void dumpJVMInfo(){
            if (!dump) {
                dump = true;
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        String logPath = Constants.JVM_LOG_PATH;
                        FileOutputStream jvmStackStream = null;
                        try {
                            jvmStackStream = new FileOutputStream(new File(logPath, "jvmStack.log"));
                            JVMUtil.jvmStackDump(jvmStackStream);
                        } catch (FileNotFoundException e) {
                            LOG.error("", "Dump JVM cache Error!", e);
                        } catch (Throwable t) {
                            LOG.error("", "Dump JVM cache Error!", t);
                        } finally {
                            if (jvmStackStream != null) {
                                try {
                                    jvmStackStream.close();
                                } catch (IOException e) {
                                    LOG.error("", "Close FileOutputStream Error!", e);
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}

package top.kwseeker.util.thread;

import top.kwseeker.util.Constants;

import java.util.concurrent.Executor;

public class ThreadPoolUtil {

    private static final ThreadPoolManager threadPoolManager = new ThreadPoolManager(
            Constants.MIN_POOL_SIZE, Constants.MAX_POOL_SIZE, Constants.THREAD_QUEUE_SIZE);

    private static Executor bossExecutor = ThreadPoolUtil.getThreadPoolManager()
            .getThreadExecutor(ThreadNameSpace.NETTY_BOSS, Constants.MIN_BOSS_POOL_SIZE, Constants.MAX_BOSS_POLL_SIZE);
    private static Executor workExecutor = ThreadPoolUtil.getThreadPoolManager()
            .getThreadExecutor(ThreadNameSpace.NETTY_WORKER, Constants.MIN_WORK_POOL_SIZE, Constants.MAX_WORK_POOL_SIZE);

    public static ThreadPoolManager getThreadPoolManager() {
        return threadPoolManager;
    }

    public static Executor getBossExecutor() {
        return bossExecutor;
    }

    public static Executor getWorkExecutor() {
        return workExecutor;
    }
}

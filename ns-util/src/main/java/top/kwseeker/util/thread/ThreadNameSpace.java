package top.kwseeker.util.thread;

public class ThreadNameSpace {

    //netty boss线程
    public static final String NETTY_BOSS = "mg-boss";

    //netty worker线程
    public static final String NETTY_WORKER = "mg-worker";

    //connection定期检查线程
    public static final String NETTY_TIMER = "mg-timer";

    public static final String getUniqueName(String serviceName) {
        return "mg-sn-" + serviceName;
    }

}

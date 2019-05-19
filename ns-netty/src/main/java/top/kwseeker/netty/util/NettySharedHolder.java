package top.kwseeker.netty.util;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import top.kwseeker.util.thread.NamedThreadFactory;
import top.kwseeker.util.thread.ThreadNameSpace;

public class NettySharedHolder {

    public static final Timer HASHED_WHEEL_TIMER = new HashedWheelTimer(new NamedThreadFactory(ThreadNameSpace.NETTY_TIMER));
}
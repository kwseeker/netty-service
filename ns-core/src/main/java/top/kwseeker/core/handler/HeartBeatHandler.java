package top.kwseeker.core.handler;

import top.kwseeker.api.MessageHandler;
import top.kwseeker.core.message.HeartbeatMessage;

/**
 * 处理心跳连接请求，服务端接收到只是打印出来
 */
public class HeartBeatHandler implements MessageHandler<HeartbeatMessage> {

    @Override
    public void handle(HeartbeatMessage message) {
        System.err.println("receive client heartbeat, time=" + System.currentTimeMillis());
    }
}

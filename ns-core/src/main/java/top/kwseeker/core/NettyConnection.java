package top.kwseeker.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Connection;
import top.kwseeker.api.SessionContext;
import top.kwseeker.api.protocol.Packet;
import top.kwseeker.core.security.CipherBox;

/**
 * NettyConnection 是通信的时候的 Channel（io.netty.channel.Channel）的包装类
 */
public  class NettyConnection implements Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyConnection.class);

    private SessionContext context;
    private Channel channel;
    private int status = 0;
    private int hbTimes;

    @Override
    public void init(Channel channel) {
        this.channel = channel;
        this.context = new SessionContext();
        this.context.changeCipher(CipherBox.INSTANCE.getRsaCipher());
    }

    @Override
    public void setSessionContext(SessionContext context) {
        this.context = context;
    }

    @Override
    public SessionContext getSessionContext() {
        return context;
    }

    @Override
    public void send(Packet packet) {
        if (packet != null) {
            if (channel.isWritable()) {
                ChannelFuture wf = channel.writeAndFlush(packet);
                wf.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            if (!channel.isActive()) {
                                LOGGER.warn("send msg failed, channel is not active clientIp={}, packet={}", channel.remoteAddress().toString(), packet);
                                ConnectionManager.INSTANCE.remove(channel);
                                channel.close();
                            }
                            LOGGER.warn("send msg failed clientIp={}, packet={}", channel.remoteAddress().toString(), packet);
                        } else {
                            LOGGER.warn("send msg success clientIp={}, packet={}", channel.remoteAddress().toString(), packet);
                        }
                    }
                });
            } else {
                LOGGER.warn("send msg failed, channel is not writable clientIp={}, packet={}", channel.remoteAddress().toString(), packet);
            }
        }
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public String getId() {
        return channel.id().asLongText();
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public int getHbTimes() {
        return hbTimes;
    }

    @Override
    public void close() {
        this.channel.close();
    }

    @Override
    public boolean isConnected() {
        return channel.isActive();
    }

    @Override
    public boolean isEnable() {
        return channel.isWritable();
    }

    public String remoteIp() {
        return channel.remoteAddress().toString();
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int increaseAndGetHbTimes() {
        return ++hbTimes;
    }

    public void resetHbTimes() {
        hbTimes = 0;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NettyConnection{" +
                "channel=" + channel +
                ", status=" + status +
                '}';
    }
}

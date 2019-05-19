package top.kwseeker.api;

import io.netty.channel.Channel;
import top.kwseeker.api.protocol.Packet;

public interface Connection {

    void init(Channel channel);

    SessionContext getSessionContext();

    void setSessionContext(SessionContext context);

    void send(Packet packet);

    Channel channel();

    String getId();

    boolean isClosed();

    boolean isOpen();

    int getHbTimes();

    void close();

    boolean isConnected();

    boolean isEnable();

    String remoteIp();
}

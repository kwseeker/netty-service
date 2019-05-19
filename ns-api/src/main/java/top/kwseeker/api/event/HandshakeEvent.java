package top.kwseeker.api.event;

import top.kwseeker.api.Connection;

public class HandshakeEvent implements Event {

    public final Connection connection;
    public final int heartbeat;

    public HandshakeEvent(Connection connection, int heartbeat) {
        this.connection = connection;
        this.heartbeat = heartbeat;
    }
}

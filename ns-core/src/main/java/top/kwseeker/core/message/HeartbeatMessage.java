package top.kwseeker.core.message;

import top.kwseeker.api.Connection;
import top.kwseeker.api.Message;

public final class HeartbeatMessage implements Message {

    private final Connection connection;

    public HeartbeatMessage(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void send() {
    }

    @Override
    public void sendRaw() {
    }
}

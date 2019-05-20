package top.kwseeker.core.message;

import io.netty.buffer.ByteBuf;
import top.kwseeker.api.Connection;
import top.kwseeker.api.protocol.Packet;

public final class FastConnectMessage extends ByteBufMessage {
    public String sessionId;
    public String deviceId;

    public FastConnectMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    @Override
    public void decode(ByteBuf body) {
        sessionId = decodeString(body);
        deviceId = decodeString(body);
    }

    @Override
    public void encode(ByteBuf body) {
        encodeString(body, sessionId);
        encodeString(body, deviceId);
    }
}

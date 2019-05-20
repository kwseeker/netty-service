package top.kwseeker.api.message;

import io.netty.buffer.ByteBuf;
import top.kwseeker.api.Connection;
import top.kwseeker.api.protocol.Command;
import top.kwseeker.api.protocol.Packet;

public class KickUserMessage extends ByteBufMessage {

    public String deviceId;
    public String userId;

    public KickUserMessage(Connection connection) {
        super(new Packet(Command.KICK.cmd), connection);
    }

    public KickUserMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    @Override
    public void decode(ByteBuf body) {
        deviceId = decodeString(body);
        userId = decodeString(body);
    }

    @Override
    public void encode(ByteBuf body) {
        encodeString(body, deviceId);
        encodeString(body, userId);
    }

    @Override
    public String toString() {
        return "KickUserMessage{" +
                "deviceId='" + deviceId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}

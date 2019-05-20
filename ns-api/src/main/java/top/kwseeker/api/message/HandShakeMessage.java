package top.kwseeker.api.message;

import io.netty.buffer.ByteBuf;
import top.kwseeker.api.Connection;
import top.kwseeker.api.protocol.Command;
import top.kwseeker.api.protocol.Packet;

public final class HandShakeMessage extends ByteBufMessage {
    public String deviceId;
    public String osName;
    public String osVersion;
    public String clientVersion;
    public byte[] iv;
    public byte[] clientKey;
    public long timestamp;

    public HandShakeMessage(Connection connection) {
        super(new Packet(Command.HANDSHAKE.cmd, genSessionId()), connection);
    }

    public HandShakeMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    @Override
    public void decode(ByteBuf body) {
        deviceId = decodeString(body);
        osName = decodeString(body);
        osVersion = decodeString(body);
        clientVersion = decodeString(body);
        iv = decodeBytes(body);
        clientKey = decodeBytes(body);
        timestamp = decodeLong(body);
    }

    public void encode(ByteBuf body) {
        encodeString(body, deviceId);
        encodeString(body, osName);
        encodeString(body, osVersion);
        encodeString(body, clientVersion);
        encodeBytes(body, iv);
        encodeBytes(body, clientKey);
        encodeLong(body, timestamp);
    }
}

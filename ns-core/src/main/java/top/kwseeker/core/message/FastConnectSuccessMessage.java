package top.kwseeker.core.message;

import io.netty.buffer.ByteBuf;
import top.kwseeker.api.Connection;
import top.kwseeker.api.protocol.Packet;

public final class FastConnectSuccessMessage extends ByteBufMessage {

    public String serverHost;
    public long serverTime;
    public int heartbeat;

    public FastConnectSuccessMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    public static FastConnectSuccessMessage from(BaseMessage src) {
        return new FastConnectSuccessMessage(src.createResponse(), src.connection);
    }

    @Override
    public void decode(ByteBuf body) {
        serverHost = decodeString(body);
        serverTime = decodeLong(body);
        heartbeat = decodeInt(body);
    }

    @Override
    public void encode(ByteBuf body) {
        encodeString(body, serverHost);
        encodeLong(body, serverTime);
        encodeInt(body, heartbeat);
    }


    public FastConnectSuccessMessage setServerHost(String serverHost) {
        this.serverHost = serverHost;
        return this;
    }

    public FastConnectSuccessMessage setServerTime(long serverTime) {
        this.serverTime = serverTime;
        return this;
    }

    public FastConnectSuccessMessage setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
        return this;
    }
}
package top.kwseeker.api.message;

import top.kwseeker.api.Connection;
import top.kwseeker.api.Constants;
import top.kwseeker.api.Message;
import top.kwseeker.api.SessionContext;
import top.kwseeker.api.protocol.Packet;
import top.kwseeker.util.IOUtils;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseMessage implements Message {

    private static final AtomicInteger ID_SEQ = new AtomicInteger();
    protected final Packet packet;
    protected final Connection connection;

    public BaseMessage(Packet packet, Connection connection) {
        this.packet = packet;
        this.connection = connection;
        this.decodeBody();
    }

    protected void decodeBody() {
        if (packet.body != null && packet.body.length > 0) {
            //1.解密
            byte[] tmp = packet.body;
            if (packet.hasFlag(Constants.CRYPTO_FLAG)) {
                SessionContext info = connection.getSessionContext();
                if (info.cipher != null) {
                    tmp = info.cipher.decrypt(tmp);
                }
            }

            //2.解压
            if (packet.hasFlag(Constants.COMPRESS_FLAG)) {
                byte[] result = IOUtils.uncompress(tmp);
                if (result.length > 0) {
                    tmp = result;
                }
            }
            packet.body = tmp;
            decode(packet.body);
        }
    }

    protected void encodeBody() {
        byte[] tmp = encode();
        if (tmp != null && tmp.length > 0) {
            //1.压缩
            if (tmp.length > Constants.COMPRESS_LIMIT) {
                byte[] result = IOUtils.compress(tmp);
                if (result.length > 0) {
                    tmp = result;
                    packet.setFlag(Constants.COMPRESS_FLAG);
                }
            }

            //2.加密
            SessionContext context = connection.getSessionContext();
            if (context.cipher != null) {
                tmp = context.cipher.encrypt(tmp);
                packet.setFlag(Constants.CRYPTO_FLAG);
            }
            packet.body = tmp;
        }
    }

    public abstract void decode(byte[] body);

    public abstract byte[] encode();

    public Packet createResponse() {
        return new Packet(packet.cmd, packet.sessionId);
    }

    @Override
    public Packet getPacket() {
        return packet;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void send() {
        encodeBody();
        connection.send(packet);
    }

    @Override
    public void sendRaw() {
        packet.body = encode();
        connection.send(packet);
    }

    protected static int genSessionId() {
        return ID_SEQ.incrementAndGet();
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
                "packet=" + packet +
                ", connection=" + connection +
                '}';
    }
}

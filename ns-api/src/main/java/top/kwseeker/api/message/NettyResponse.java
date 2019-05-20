package top.kwseeker.api.message;

import top.kwseeker.api.Connection;
import top.kwseeker.api.Constants;
import top.kwseeker.api.Response;
import top.kwseeker.api.SessionContext;
import top.kwseeker.api.protocol.Packet;
import top.kwseeker.util.IOUtils;

public class NettyResponse implements Response {

    private final Packet packet;
    private final Connection connection;

    public NettyResponse(Packet packet, Connection connection) {
        this.packet = packet;
        this.connection = connection;
    }

    public void send(byte[] body) {
        byte[] tmp = body;
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
        connection.send(packet);
    }

    public void sendRaw(byte[] body) {
        packet.body = body;
        connection.send(packet);
    }


    public void sendError(byte[] reason) {
        packet.body = reason;
        connection.send(packet);
    }

    @Override
    public void send(String body) {
        send(body.getBytes(Constants.UTF_8));
    }

    @Override
    public void sendRaw(String body) {
        sendRaw(body.getBytes(Constants.UTF_8));
    }

    @Override
    public void sendError(String reason) {
        sendError(reason.getBytes(Constants.UTF_8));
    }
}


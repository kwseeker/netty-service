package top.kwseeker.core.message;

import top.kwseeker.api.Connection;
import top.kwseeker.api.Constants;
import top.kwseeker.api.Message;
import top.kwseeker.api.SessionContext;
import top.kwseeker.api.protocol.Packet;
import top.kwseeker.util.IOUtils;

public abstract class BaseMessage implements Message {

    protected final Packet message;
    protected final Connection connection;

    public BaseMessage(Packet message, Connection connection) {
        this.message = message;
        this.connection = connection;
        this.decodeBody();
    }

    protected void decodeBody() {
        if (message.body != null && message.body.length > 0) {
            //1.解密
            byte[] tmp = message.body;
            if (message.hasFlag(Constants.CRYPTO_FLAG)) {
                SessionContext info = connection.getSessionContext();
                if (info.cipher != null) {
                    tmp = info.cipher.decrypt(tmp);
                }
            }

            //2.解压
            if (message.hasFlag(Constants.COMPRESS_FLAG)) {
                byte[] result = IOUtils.uncompress(tmp);
                if (result.length > 0) {
                    tmp = result;
                }
            }
            message.body = tmp;
            decode(message.body);
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
                    message.setFlag(Constants.COMPRESS_FLAG);
                }
            }

            //2.加密
            SessionContext context = connection.getSessionContext();
            if (context != null && context.cipher != null) {
                tmp = context.cipher.encrypt(tmp);
                message.setFlag(Constants.CRYPTO_FLAG);
            }
            message.body = tmp;
        }
    }

    public abstract void decode(byte[] body);

    public abstract byte[] encode();

    @Override
    public Connection getConnection() {
        return connection;
    }

    public Packet createResponse() {
        return new Packet(message.cmd, message.sessionId);
    }

    @Override
    public void send() {
        encodeBody();
        connection.send(message);
    }

    @Override
    public void sendRaw() {
        message.body = encode();
        connection.send(message);
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
                "message=" + message +
                ", connection=" + connection +
                '}';
    }
}

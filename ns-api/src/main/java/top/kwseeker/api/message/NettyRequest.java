package top.kwseeker.api.message;

import top.kwseeker.api.*;
import top.kwseeker.api.protocol.Command;
import top.kwseeker.api.protocol.Packet;
import top.kwseeker.util.IOUtils;

public class NettyRequest implements Request {

    private final Packet message;
    private final Connection connection;
    private Command command;
    private byte[] body;

    public NettyRequest(Packet message, Connection connection) {
        this.message = message;
        this.connection = connection;
    }

    public Command getCommand() {
        return command == null ? command = Command.toCMD(message.cmd) : command;
    }

    public byte[] getBody() {
        if (message.body == null) return null;
        if (body == null) {
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
            this.body = tmp;
        }
        return body;
    }

    public Connection getConnection() {
        return connection;
    }

    public Response getResponse() {
        Packet packet = new Packet(this.message.cmd, this.message.sessionId);
        return new NettyResponse(packet, connection);
    }
}

package top.kwseeker.api.message;

import com.google.common.base.Strings;
import top.kwseeker.api.Connection;
import top.kwseeker.api.Constants;
import top.kwseeker.api.protocol.Command;
import top.kwseeker.api.protocol.Packet;

public class BindUserMessage extends BaseMessage {

    public String userId;

    public BindUserMessage(Connection connection) {
        super(new Packet(Command.BIND.cmd, genSessionId()), connection);
    }

    public BindUserMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    @Override
    public void decode(byte[] body) {
        if (body != null && body.length > 0) {
            userId = new String(body, Constants.UTF_8);
        }
    }

    @Override
    public byte[] encode() {
        return Strings.isNullOrEmpty(userId) ? null : userId.getBytes(Constants.UTF_8);
    }
}

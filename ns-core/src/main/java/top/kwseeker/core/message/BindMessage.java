package top.kwseeker.core.message;

import com.google.common.base.Strings;
import top.kwseeker.api.Connection;
import top.kwseeker.api.Constants;
import top.kwseeker.api.protocol.Packet;

public class BindMessage extends BaseMessage {

    public String userId;

    public BindMessage(Packet message, Connection connection) {
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

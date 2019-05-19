package top.kwseeker.core.message;

import com.google.common.base.Strings;
import top.kwseeker.api.Connection;
import top.kwseeker.api.Constants;
import top.kwseeker.api.protocol.Packet;

public class SuccessMessage extends BaseMessage {

    public String data;

    public SuccessMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    @Override
    public void decode(byte[] body) {
        if (body != null && body.length > 0) {
            data = new String(body, Constants.UTF_8);
        }
    }

    @Override
    public byte[] encode() {
        return Strings.isNullOrEmpty(data) ? null : data.getBytes(Constants.UTF_8);
    }

    public static SuccessMessage from(BaseMessage message) {
        return new SuccessMessage(message.createResponse(), message.connection);
    }

    public SuccessMessage setData(String data) {
        this.data = data;
        return this;
    }
}

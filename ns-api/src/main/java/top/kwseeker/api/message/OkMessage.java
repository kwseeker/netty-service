package top.kwseeker.api.message;

import com.google.common.base.Strings;
import top.kwseeker.api.Connection;
import top.kwseeker.api.Constants;
import top.kwseeker.api.protocol.Packet;

public final class OkMessage extends BaseMessage {

    public String data;

    public OkMessage(Packet message, Connection connection) {
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

    public static OkMessage from(BaseMessage message) {
        return new OkMessage(message.createResponse(), message.connection);
    }

    public OkMessage setData(String data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "OkMessage{" +
                "data='" + data + '\'' +
                "packet='" + packet + '\'' +
                '}';
    }
}

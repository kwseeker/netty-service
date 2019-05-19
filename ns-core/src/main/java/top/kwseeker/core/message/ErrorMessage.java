package top.kwseeker.core.message;

import io.netty.buffer.ByteBuf;
import top.kwseeker.api.Connection;
import top.kwseeker.api.protocol.Command;
import top.kwseeker.api.protocol.Packet;

public class ErrorMessage extends ByteBufMessage {

    public String reason;
    public byte errorCode;

    public ErrorMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    public static ErrorMessage from(BaseMessage src) {
        return new ErrorMessage(new Packet(Command.ERROR.cmd, src.message.sessionId), src.connection);
    }

    public ErrorMessage setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public ErrorMessage setErrorCode(byte errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    @Override
    public void decode(ByteBuf body) {
        reason = decodeString(body);
        errorCode = decodeByte(body);
    }

    @Override
    public void encode(ByteBuf body) {
        encodeString(body, reason);
        encodeByte(body, errorCode);
    }

    @Override
    public void send() {
        super.sendRaw();
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "reason='" + reason + '\'' +
                ", errorCode=" + errorCode +
                ", message=" + message +
                '}';
    }
}

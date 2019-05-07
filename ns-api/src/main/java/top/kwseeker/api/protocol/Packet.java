package top.kwseeker.api.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Netty服务通信接收的包的数据结构
 * length(4)+cmd(1)+cc(2)+flags(1)+sessionId(4)+lrc(1)+body(n)
 */
public class Packet {
    public static final byte HB_PACKET = '\n';

    public byte cmd;    //命令
    public short cc;    //校验码
    public byte flags;  //特性，是否加密、压缩
    public int sessionId;   //会话ID
    public byte lrc;        //校验，纵向冗余校验
    public byte[] body;

    public Packet(byte cmd) {
        this.cmd = cmd;
    }

    public Packet(byte cmd, int sessionId) {
        this.cmd = cmd;
        this.sessionId = sessionId;
    }

    public int getBodyLength() {
        return body == null ? 0 : body.length;
    }

    public String getStringBody() {
        return body == null ? "" : new String(body, StandardCharsets.UTF_8);
    }

    public void setFlag(byte flag) {
        this.flags |= flag;
    }

    public boolean hasFlag(byte flag) {
        return (flags & flag) != 0;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "cmd=" + cmd +
                ", cc=" + cc +
                ", flags=" + flags +
                ", sessionId=" + sessionId +
                ", lrc=" + lrc +
                ", body=" + Arrays.toString(body) +
                '}';
    }

    public static ByteBuf getHBPacket() {
        return Unpooled.buffer(1).writeByte(HB_PACKET);
    }
}

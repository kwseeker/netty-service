package top.kwseeker.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.protocol.Packet;

/**
 * 后台返回的数据编码为ByteBuf数据流
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        out.writeInt(packet.getBodyLength());
        out.writeByte(packet.cmd);
        out.writeShort(packet.cc);
        out.writeByte(packet.flags);
        out.writeInt(packet.sessionId);
        out.writeByte(packet.lrc);
        if(packet.getBodyLength() > 0) {
            out.writeBytes(packet.body);
        }
    }
}

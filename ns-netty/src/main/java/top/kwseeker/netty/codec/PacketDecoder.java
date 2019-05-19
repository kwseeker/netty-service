package top.kwseeker.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Constants;
import top.kwseeker.api.protocol.Packet;

import java.util.List;

/**
 * Channel 解码器
 * 从ByteBuf中一帧一帧地读取数据并解码成List
 */
public class PacketDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        decodeFrames(in, out);
    }

    /**
     * 数据包解码，数据包是一帧一帧发送的，每个帧的数据结构为Packet
     * @param in
     * @param out 解码之后帧转换为的对象的List
     * @throws Exception
     */
    //writeIndex到数组尾部后怎么处理？
    private void decodeFrames(ByteBuf in, List<Object> out) throws Exception {
        LOGGER.debug("数据包解码");
        //拆包成帧
        while(in.readableBytes() >= Constants.HEADER_LEN) {
            in.markReaderIndex();   //标记读取位置，用于读取异常恢复到该位置
            out.add(decodeFrame(in));
        }
    }

    private Packet decodeFrame(ByteBuf in) throws Exception {
        int bufferSize = in.readableBytes();
        int bodyLength = in.readInt();  //数据包头部是body长度
        if(bufferSize < bodyLength + Constants.HEADER_LEN) {
            throw new DecoderException("无效的数据帧");
        }
        return readPacket(in, bodyLength);
    }

    //帧数据转换为Packet对象
    private Packet readPacket(ByteBuf in, int bodyLength) {
        return Packet.readPacketFromByteBuf(in, bodyLength);
    }
}

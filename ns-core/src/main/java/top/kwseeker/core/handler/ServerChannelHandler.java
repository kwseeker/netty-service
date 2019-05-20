package top.kwseeker.core.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Connection;
import top.kwseeker.api.PacketReceiver;
import top.kwseeker.api.protocol.Packet;
import top.kwseeker.core.ConnectionManager;
import top.kwseeker.core.NettyConnection;

@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerChannelHandler.class);

    private final PacketReceiver packetReceiver;

    public ServerChannelHandler(PacketReceiver packetReceiver) {
        this.packetReceiver = packetReceiver;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Connection connection = ConnectionManager.INSTANCE.get(ctx.channel());
        packetReceiver.onReceive((Packet) msg, connection);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ConnectionManager.INSTANCE.remove(ctx.channel());
        LOGGER.error(ctx.channel().remoteAddress() + ", exceptionCaught", cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.warn(ctx.channel().remoteAddress() + ",  channelActive");
        Connection connection = new NettyConnection();
        connection.init(ctx.channel());
        ConnectionManager.INSTANCE.add(connection);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.warn(ctx.channel().remoteAddress() + ",  channelInactive");
        ConnectionManager.INSTANCE.remove(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent stateEvent = (IdleStateEvent) evt;
            switch (stateEvent.state()) {
                case READER_IDLE:
                    ConnectionManager.INSTANCE.remove(ctx.channel());
                    ctx.close();
                    LOGGER.warn("heartbeat read timeout, chanel closed!");
                    break;
                case WRITER_IDLE:
                    ctx.writeAndFlush(Packet.getHBPacket());
                    LOGGER.warn("heartbeat write timeout, do write an EOL.");
                    break;
                case ALL_IDLE:
            }
        } else {
            LOGGER.warn("One user event Triggered. evt=" + evt);
            super.userEventTriggered(ctx, evt);
        }
    }
}

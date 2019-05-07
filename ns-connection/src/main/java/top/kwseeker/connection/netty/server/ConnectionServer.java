package top.kwseeker.connection.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.connection.netty.codec.PacketDecoder;
import top.kwseeker.connection.netty.codec.PacketEncoder;
import top.kwseeker.connection.netty.handler.ConnectionReqHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionServer.class);

    private final AtomicBoolean startFlag = new AtomicBoolean(false);
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public ConnectionServer(int port) {
        this.port = port;
    }

    public void start() {
        if(!startFlag.compareAndSet(false, true)) {
            return;
        }

        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            final ConnectionReqHandler connectionReqHandler = new ConnectionReqHandler();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)      //TODO: 拓展支持 EpollServerSocketChannel
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new PacketDecoder())
                                    .addLast(new PacketEncoder())
                                    .addLast(connectionReqHandler);
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(port).sync();
            LOGGER.info("Server started on port " + port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("Server start exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        LOGGER.info("Server stop now");
        this.startFlag.set(false);
        if(workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if(bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
    }
}

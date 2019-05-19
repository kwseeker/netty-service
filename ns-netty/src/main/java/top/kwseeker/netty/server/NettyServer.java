package top.kwseeker.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Server;
import top.kwseeker.netty.codec.PacketDecoder;
import top.kwseeker.netty.codec.PacketEncoder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Netty服务端
 *
 * 1）注册用于业务处理的ChannelHandler
 * 2）注册通用的通信包的编解码器 PacketEncoder/PacketDecoder
 * 3）Netty Server配置项
 */
public class NettyServer implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    private final AtomicBoolean startFlag = new AtomicBoolean(false);
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private final ChannelHandler channelHandler;

    public NettyServer(int port) {
        this.port = port;
        this.channelHandler = null;
    }

    public NettyServer(int port, ChannelHandler channelHandler) {
        this.port = port;
        this.channelHandler = channelHandler;
    }

    @Override
    public void init() {
    }

    @Override
    public boolean isRunning() {
        return startFlag.get();
    }

    public void start() {
        if(!startFlag.compareAndSet(false, true)) {
            return;
        }

        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)      //TODO: 拓展支持 EpollServerSocketChannel
                    .option(ChannelOption.SO_BACKLOG, 1024)     //
                    .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new PacketDecoder())
                                    .addLast(new PacketEncoder())
                                    .addLast(channelHandler);
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(port).sync();
            LOGGER.info("Server started on port " + port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("Server start exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            LOGGER.info("Server stop on port " + port);
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

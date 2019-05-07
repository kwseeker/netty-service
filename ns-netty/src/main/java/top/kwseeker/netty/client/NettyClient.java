package top.kwseeker.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Client;
import top.kwseeker.netty.codec.PacketDecoder;
import top.kwseeker.netty.codec.PacketEncoder;

import java.net.InetSocketAddress;

public class NettyClient implements Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    private final ChannelHandler handler;
    private final String host;
    private final int port;
    private Channel channel;
    //
    private int hbTimes = 0;

    public NettyClient(final String host, final int port, ChannelHandler handler) {
        this.host = host;
        this.port = port;
        this.handler = handler;
    }

    @Override
    public void init() {
        this.close("re init");
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)//
                .option(ChannelOption.TCP_NODELAY, true)    //
                .option(ChannelOption.SO_REUSEADDR, true)   //
                .option(ChannelOption.SO_KEEPALIVE, true)   //
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)    //
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 4000);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {     //
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new PacketDecoder());
                ch.pipeline().addLast(new PacketEncoder());
                ch.pipeline().addLast(handler);
            }
        });

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        if (future.awaitUninterruptibly(4000) && future.isSuccess() && future.channel().isActive()) {
            channel = future.channel();
        } else {
            future.cancel(true);
            future.channel().close();
            LOGGER.warn("[remoting] failure to connect:" + host + "," + port);
        }
    }

    @Override
    public void start() {
        if (channel != null) {
            try {
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                LOGGER.error("" + e.getMessage());
            }
        }
    }

    @Override
    public void close(String cause) {
        if (!StringUtils.isBlank(cause) && !"null".equals(cause.trim())) {
            LOGGER.error("close channel:" + cause);
        }
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public boolean isEnabled() {
        return channel.isWritable();
    }

    @Override
    public boolean isConnected() {
        return channel.isActive();
    }

    @Override
    public void resetHbTimes() {
        hbTimes = 0;
    }

    @Override
    public int increaseAndGetHbTimes() {
        return ++hbTimes;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String getUri() {
        return host + ":" + port;
    }
}

package top.kwseeker.netty.client;

import io.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Client;

/**
 * 创建客户端
 */
public class NettyClientFactory extends AbstractNettyClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientFactory.class);

    public static final NettyClientFactory INSTANCE = new NettyClientFactory();

    protected Client createClient(final String host, final int port, final ChannelHandler handler) throws Exception {
        return new NettyClient(host, port, handler);
    }

    public void remove(final Client client) {
        super.remove(client);
    }

}
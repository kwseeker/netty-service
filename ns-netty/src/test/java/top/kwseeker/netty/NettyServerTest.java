package top.kwseeker.netty;

import io.netty.channel.ChannelHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.PacketReceiver;
import top.kwseeker.core.MessageDispatcher;
import top.kwseeker.core.handler.ServerChannelHandler;
import top.kwseeker.netty.server.NettyServer;

public class NettyServerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientTest.class);

    @Test
    public void testStart() throws Exception {

        PacketReceiver packetReceiver = new MessageDispatcher();
        ChannelHandler handler = new ServerChannelHandler(packetReceiver);

        final NettyServer server = new NettyServer(3000, handler);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    server.stop();
                } catch (Exception e) {
                }
            }
        });
    }
}

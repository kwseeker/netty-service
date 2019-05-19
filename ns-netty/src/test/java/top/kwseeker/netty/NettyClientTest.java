package top.kwseeker.netty;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Client;
import top.kwseeker.netty.client.NettyClientFactory;

public class NettyClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientTest.class);

    private String host = "127.0.0.1";
    private int port = 3000;
    private ClientChannelHandler handler = new ClientChannelHandler();

    @Test
    public void testClient() throws Exception {

        Client client = NettyClientFactory.INSTANCE.get(host, port, handler);

        client.init();
        client.start();

        LOGGER.error(ToStringBuilder.reflectionToString(client, ToStringStyle.MULTI_LINE_STYLE));
    }
}

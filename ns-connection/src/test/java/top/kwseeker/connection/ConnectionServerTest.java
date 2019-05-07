package top.kwseeker.connection;

import org.junit.Test;
import top.kwseeker.connection.netty.server.ConnectionServer;

public class ConnectionServerTest {

    @Test
    public void testStart() {
        ConnectionServer server = new ConnectionServer(8080);
        server.start();
    }
}

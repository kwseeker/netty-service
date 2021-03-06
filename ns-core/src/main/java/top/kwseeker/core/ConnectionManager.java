package top.kwseeker.core;

import com.google.common.eventbus.Subscribe;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * 连接管理器
 * 1）缓存连接
 */
public class ConnectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    private static final String IDLE_HANDLER_NAME = "heartbeatHandler";
    public static final ConnectionManager INSTANCE = new ConnectionManager();

    public ConnectionManager() {
        EventBus.INSTANCE.register(this);
    }

    //处理订阅事件
    @Subscribe
    public void onSomeEvent(Object event) {
        //handle logic
    }
    //public void onHandshakeSuccess(HandshakeEvent event) {
    //    int r = event.heartbeat + 3000;
    //    int w = event.heartbeat + 3000;
    //    Channel channel = event.connection.channel();
    //    channel.pipeline().addFirst(new IdleStateHandler(r, w, 0, TimeUnit.MILLISECONDS));
    //    LOGGER.warn("NettyChannel setHeartbeat readTimeout={}, writeTimeout={}", r, w);
    //}

    //所有连接缓存到ConcurrentHashMap数据结构中
    private final ConcurrentMap<String, Connection> connections = new ConcurrentHashMap<>();

    public Connection get(final String channelId) throws ExecutionException {
        return connections.get(channelId);
    }

    public Connection get(final Channel channel) {
        return connections.get(channel.id().asLongText());
    }

    public void add(Connection connection) {
        connections.putIfAbsent(connection.getId(), connection);
    }

    public void add(Channel channel) {
        Connection connection = new NettyConnection();
        connection.init(channel);
        connections.putIfAbsent(connection.getId(), connection);
    }

    public void remove(Connection connection) {
        connections.remove(connection.getId());
    }

    public void remove(Channel channel) {
        connections.remove(channel.id().asLongText());
    }

    public List<String> getConnectionIds() {
        return new ArrayList<String>(connections.keySet());
    }

    public List<Connection> getConnections() {
        return new ArrayList<Connection>(connections.values());
    }

}

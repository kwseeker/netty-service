package top.kwseeker.netty.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import io.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Client;

import java.text.MessageFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNettyClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNettyClientFactory.class);

    private static final String FORMAT = "%s:%s";
    private final Cache<String, Client> cachedClients = CacheBuilder.newBuilder()
            .maximumSize(2 << 17)   //最大支持缓存65535*2个连接
            .expireAfterAccess(60, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String, Client>() {
                @Override
                public void onRemoval(RemovalNotification<String, Client> notification) {
                    if (notification.getValue().isConnected()) {
                        notification.getValue().close("[Remoting] removed from cache");
                    }
                }
            })
            .build();

    public Client get(final String remoteHost, final int port, final ChannelHandler handler) throws Exception {
        final String key = String.format(FORMAT, remoteHost, port);
        Client client = cachedClients.get(key, new Callable<Client>() {
            @Override
            public Client call() throws Exception {
                Client client = createClient(remoteHost, port, handler);
                return client;
            }
        });
        if (client == null) {
            cachedClients.invalidate(key);
            return null;
        }
        return client;
    }

    public Client get(final String remoteHost, final int port) throws Exception {
        return get(remoteHost, port, null);
    }


    protected Client createClient(final String remoteHost, final int port) throws Exception {
        return createClient(remoteHost, port, null);
    }

    protected abstract Client createClient(final String remoteHost, final int port, ChannelHandler handler) throws Exception;

    public void remove(Client client) {
        if (client != null) {
            cachedClients.invalidate(client.getUri());
            LOGGER.warn(MessageFormat.format("[Remoting] {0} is removed", client));
        }
    }
}

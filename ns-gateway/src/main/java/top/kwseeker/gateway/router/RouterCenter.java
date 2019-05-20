package top.kwseeker.gateway.router;

import top.kwseeker.api.ClientLocation;
import top.kwseeker.api.Connection;
import top.kwseeker.api.SessionContext;
import top.kwseeker.api.message.KickUserMessage;
import top.kwseeker.api.router.Router;

public class RouterCenter {

    public static final RouterCenter INSTANCE = new RouterCenter();

    private final LocalRouterManager localRouterManager = new LocalRouterManager();
    private final RemoteRouterManager remoteRouterManager = new RemoteRouterManager();

    //注册用户和链接
    public boolean register(String userId, Connection connection) {
        ClientLocation connConfig = ClientLocation.from(connection.getSessionContext());

        LocalRouter localRouter = new LocalRouter(connection);
        RemoteRouter remoteRouter = new RemoteRouter(connConfig);

        LocalRouter oldLocalRouter = localRouterManager.register(userId, localRouter);
        RemoteRouter oldRemoteRouter = remoteRouterManager.register(userId, remoteRouter);
        if (oldLocalRouter != null) {
            kickLocalUser(userId, oldLocalRouter);
        }

        if (oldRemoteRouter != null) {
            kickRemoteUser(userId, oldRemoteRouter);
        }
        return true;
    }

    public boolean unRegister(String userId) {
        localRouterManager.unRegister(userId);
        remoteRouterManager.unRegister(userId);
        return true;
    }

    public Router<?> lookup(String userId) {
        LocalRouter local = localRouterManager.lookup(userId);
        if (local != null) return local;
        RemoteRouter remote = remoteRouterManager.lookup(userId);
        return remote;
    }

    private void kickLocalUser(String userId, LocalRouter router) {
        Connection connection = router.getRouteValue();
        SessionContext context = connection.getSessionContext();
        KickUserMessage message = new KickUserMessage(connection);
        message.deviceId = context.deviceId;
        message.userId = userId;
        message.send();
    }

    private void kickRemoteUser(String userId, RemoteRouter router) {
        //send msg to zk
    }
}

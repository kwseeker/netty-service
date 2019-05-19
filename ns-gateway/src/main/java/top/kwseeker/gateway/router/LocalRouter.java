package top.kwseeker.gateway.router;

import top.kwseeker.api.Connection;
import top.kwseeker.api.router.Router;
import top.kwseeker.api.router.RouterInfo;

public class LocalRouter implements Router {

    private final Connection connection;

    public LocalRouter(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnect() {
        return connection;
    }

    public RouterInfo getRouterInfo() {
        return null;
    }
}

package top.kwseeker.gateway.router;

import top.kwseeker.api.Connection;
import top.kwseeker.api.router.Router;

public class LocalRouter implements Router<Connection> {
    private final Connection connection;

    public LocalRouter(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getRouteValue() {
        return connection;
    }

    @Override
    public RouterType getRouteType() {
        return RouterType.LOCAL;
    }
}

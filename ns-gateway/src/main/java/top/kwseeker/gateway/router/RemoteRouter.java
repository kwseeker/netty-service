package top.kwseeker.gateway.router;

import top.kwseeker.api.Connection;
import top.kwseeker.api.router.Router;
import top.kwseeker.api.router.RouterInfo;

public class RemoteRouter implements Router {

    private final RouterInfo routerInfo;

    public RemoteRouter(RouterInfo routerInfo) {
        this.routerInfo = routerInfo;
    }

    public Connection getConnect() {
        return null;
    }

    public RouterInfo getRouterInfo() {
        return null;
    }
}

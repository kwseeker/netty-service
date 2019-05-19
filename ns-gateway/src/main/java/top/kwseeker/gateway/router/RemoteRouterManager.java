package top.kwseeker.gateway.router;

import top.kwseeker.api.router.Router;
import top.kwseeker.api.router.RouterManager;

public class RemoteRouterManager implements RouterManager {

    @Override
    public boolean publish(String userId, Router route) {
        return true;
    }

    @Override
    public boolean unPublish(String userId) {
        return true;
    }

    @Override
    public Router getRouter(String userId) {
        return null;
    }
}

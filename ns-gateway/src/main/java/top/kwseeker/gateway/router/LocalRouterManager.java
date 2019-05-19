package top.kwseeker.gateway.router;

import top.kwseeker.api.router.Router;
import top.kwseeker.api.router.RouterManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRouterManager implements RouterManager {
    private final Map<String, Router> routerMap = new ConcurrentHashMap<>();

    @Override
    public boolean publish(String userId, Router route) {
        routerMap.put(userId, route);
        return true;
    }

    @Override
    public boolean unPublish(String userId) {
        routerMap.remove(userId);
        return true;
    }

    @Override
    public Router getRouter(String userId) {
        return routerMap.get(userId);
    }
}

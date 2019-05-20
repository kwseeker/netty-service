package top.kwseeker.gateway.router;

import top.kwseeker.api.router.RouterManager;

public class RemoteRouterManager implements RouterManager<RemoteRouter> {

    @Override
    public RemoteRouter register(String userId, RemoteRouter route) {
        return null;
    }

    @Override
    public boolean unRegister(String userId) {
        return true;
    }

    @Override
    public RemoteRouter lookup(String userId) {
        return null;
    }
}

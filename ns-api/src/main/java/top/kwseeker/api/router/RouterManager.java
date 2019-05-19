package top.kwseeker.api.router;

public interface RouterManager {

    boolean publish(String userId, Router route);

    boolean unPublish(String userId);

    Router getRouter(String userId);
}

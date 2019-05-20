package top.kwseeker.api.router;

public interface RouterManager<R extends Router> {

    //注册路由
    R register(String userId, R route);

    //删除路由
    boolean unRegister(String userId);

    //查询路由
    R lookup(String userId);
}

package top.kwseeker.api.router;

import top.kwseeker.api.Connection;

public interface Router {

    Connection getConnect();

    RouterInfo getRouterInfo();
}

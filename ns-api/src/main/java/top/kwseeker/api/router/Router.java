package top.kwseeker.api.router;

import top.kwseeker.api.Connection;

public interface Router<T> {

    T getRouteValue();

    RouterType getRouteType();

    enum RouterType {
        LOCAL,
        REMOTE
    }
}

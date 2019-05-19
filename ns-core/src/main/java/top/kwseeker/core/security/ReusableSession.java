package top.kwseeker.core.security;

import top.kwseeker.api.SessionContext;

public final class ReusableSession {
    public String sessionId;
    public long expireTime;
    public SessionContext sessionContext;
}

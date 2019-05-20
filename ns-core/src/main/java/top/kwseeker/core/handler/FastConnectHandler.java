package top.kwseeker.core.handler;

import top.kwseeker.api.Constants;
import top.kwseeker.api.MessageHandler;
import top.kwseeker.api.message.ErrorMessage;
import top.kwseeker.api.message.FastConnectMessage;
import top.kwseeker.api.message.FastConnectSuccessMessage;
import top.kwseeker.core.security.ReusableSession;
import top.kwseeker.core.security.ReusableSessionManager;
import top.kwseeker.util.NetUtil;

public final  class FastConnectHandler implements MessageHandler<FastConnectMessage> {

    @Override
    public void handle(FastConnectMessage message) {
        ReusableSession session = ReusableSessionManager.INSTANCE.getSession(message.sessionId);
        if (session == null) {
            ErrorMessage.from(message).setReason("token expire").send();
        } else if (!session.sessionContext.deviceId.equals(message.deviceId)) {
            ErrorMessage.from(message).setReason("error device").send();
        } else {
            message.getConnection().setSessionContext(session.sessionContext);
            FastConnectSuccessMessage
                    .from(message)
                    .setServerHost(NetUtil.getLocalIp())
                    .setServerTime(System.currentTimeMillis())
                    .setHeartbeat(Constants.HEARTBEAT_TIME)
                    .send();
        }
    }
}
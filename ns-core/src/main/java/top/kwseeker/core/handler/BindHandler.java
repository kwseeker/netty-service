package top.kwseeker.core.handler;

import com.google.common.base.Strings;
import top.kwseeker.api.MessageHandler;
import top.kwseeker.api.SessionContext;
import top.kwseeker.core.message.BindMessage;
import top.kwseeker.core.message.ErrorMessage;
import top.kwseeker.core.message.SuccessMessage;
import top.kwseeker.gateway.router.RouterCenter;

public final class BindHandler implements MessageHandler<BindMessage> {

    @Override
    public void handle(BindMessage message) {
        if (Strings.isNullOrEmpty(message.userId)) {
            ErrorMessage.from(message).setReason("invalid param").send();
            return;
        }
        SessionContext context = message.getConnection().getSessionContext();
        if (context.handshakeOk()) {
            boolean success = RouterCenter.INSTANCE.publish(message.userId, message.getConnection());
            if (success) {
                SuccessMessage.from(message).setData("bind success").send();
                //TODO kick user
            } else {
                ErrorMessage.from(message).setReason("bind failed").send();
            }
        } else {
            ErrorMessage.from(message).setReason("not handshake").send();
        }
    }
}

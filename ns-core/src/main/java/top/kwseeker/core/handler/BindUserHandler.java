package top.kwseeker.core.handler;

import com.google.common.base.Strings;
import top.kwseeker.api.MessageHandler;
import top.kwseeker.api.SessionContext;
import top.kwseeker.api.message.BindUserMessage;
import top.kwseeker.api.message.ErrorMessage;
import top.kwseeker.api.message.OkMessage;
import top.kwseeker.gateway.router.RouterCenter;

public final class BindUserHandler implements MessageHandler<BindUserMessage> {

    @Override
    public void handle(BindUserMessage message) {
        if (Strings.isNullOrEmpty(message.userId)) {
            ErrorMessage.from(message).setReason("invalid param").send();
            return;
        }
        SessionContext context = message.getConnection().getSessionContext();
        if (context.handshakeOk()) {
            boolean success = RouterCenter.INSTANCE.register(message.userId, message.getConnection());
            if (success) {
                OkMessage.from(message).setData("bind success").send();
            } else {
                ErrorMessage.from(message).setReason("bind failed").send();
            }
        } else {
            ErrorMessage.from(message).setReason("not handshake").send();
        }
    }
}

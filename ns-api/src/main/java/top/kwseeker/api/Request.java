package top.kwseeker.api;

import top.kwseeker.api.protocol.Command;

public interface Request {

    Command getCommand();

    byte[] getBody();

    Connection getConnection();

    Response getResponse();
}

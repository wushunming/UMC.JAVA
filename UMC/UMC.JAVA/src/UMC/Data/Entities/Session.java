package UMC.Data.Entities;

import java.util.Date;
import java.util.UUID;

public class Session {
    public String SessionKey;



    public UUID user_id;


    public String Content;


    public String ContentType;


    public Date UpdateTime;


    public String DeviceToken;

    public Session setSessionKey(String sessionKey) {
        SessionKey = sessionKey;
        return this;
    }

    public Session setUser_id(UUID user_id) {
        this.user_id = user_id;
        return this;
    }

    public Session setContent(String content) {
        Content = content;
        return this;
    }

    public Session setContentType(String contentType) {
        ContentType = contentType;
        return this;
    }

    public Session setUpdateTime(Date updateTime) {
        UpdateTime = updateTime;
        return this;
    }

    public Session setDeviceToken(String deviceToken) {
        DeviceToken = deviceToken;
        return this;
    }
}

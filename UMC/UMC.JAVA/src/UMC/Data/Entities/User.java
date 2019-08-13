package UMC.Data.Entities;

import java.util.Date;
import java.util.UUID;

public class User {
    public String Username;
    public String Alias;
    public Integer Flags;
    public Date RegistrTime;
    public Date ActiveTime;
    public Integer VerifyTimes;
    public UUID SessionKey;
    public UUID Id;
    public Boolean IsMember;

    public User setUsername(String username) {
        Username = username;
        return this;
    }

    public User setAlias(String alias) {
        Alias = alias;
        return this;
    }

    public User setFlags(Integer flags) {
        Flags = flags;
        return this;
    }

    public User setRegistrTime(Date registrTime) {
        RegistrTime = registrTime;
        return this;
    }

    public User setActiveTime(Date activeTime) {
        ActiveTime = activeTime;
        return this;
    }

    public User setVerifyTimes(Integer verifyTimes) {
        VerifyTimes = verifyTimes;
        return this;
    }

    public User setSessionKey(UUID sessionKey) {
        SessionKey = sessionKey;
        return this;
    }

    public User setId(UUID id) {
        Id = id;
        return this;
    }

    public User setMember(Boolean member) {
        IsMember = member;
        return this;
    }
}

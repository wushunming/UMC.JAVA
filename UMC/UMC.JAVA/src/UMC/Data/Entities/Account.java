package UMC.Data.Entities;

import java.util.UUID;

public class Account {

    public UUID user_id;


    public String Name;

    public Integer Type;

    public Integer Flags;

    public String ForId;

    public String ConfigData;

    public Account setUser_id(UUID user_id) {
        this.user_id = user_id;
        return this;
    }

    public Account setName(String name) {
        Name = name;
        return this;
    }

    public Account setType(Integer type) {
        Type = type;
        return this;
    }

    public Account setFlags(Integer flags) {
        Flags = flags;
        return this;
    }

    public Account setForId(String forId) {
        ForId = forId;
        return this;
    }

    public Account setConfigData(String configData) {
        ConfigData = configData;
        return this;
    }
}
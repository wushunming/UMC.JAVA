package UMC.Data.Entities;

import java.util.UUID;

public class Role {
    public UUID Id;
    public String Rolename;
    public String Explain;

    public Role setId(UUID id) {
        Id = id;
        return this;
    }

    public Role setRolename(String rolename) {
        Rolename = rolename;
        return this;
    }

    public Role setExplain(String explain) {
        Explain = explain;
        return this;
    }
}

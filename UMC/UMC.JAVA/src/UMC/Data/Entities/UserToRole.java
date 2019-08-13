package UMC.Data.Entities;

import java.security.Guard;
import java.util.UUID;

public class UserToRole {
    public UUID role_id;
    public UUID user_id;

    public UserToRole setRole_id(UUID role_id) {
        this.role_id = role_id;
        return this;
    }

    public UserToRole setUser_id(UUID user_id) {
        this.user_id = user_id;
        return this;
    }
}

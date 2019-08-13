package UMC.Security;


import UMC.Data.Configuration;
import UMC.Data.Utility;

import java.util.*;

public class AccessToken {
    public String ContentType;

    public Integer Timeout;

    public AccessToken(UUID tmpId)
    {
        this.Id = tmpId;
        this.ActiveTime = 0l;
    }

    public AccessToken() {
        this.ActiveTime = 0l;

    }

    public String Username;
    public UUID SId;
    public UUID Id;
    public String Roles;
    public Long ActiveTime;

    public Map<String, String> Data = new HashMap<>();

    public static UUID token()
    {

        Principal data = Principal.current();
        if (data != null) {
            AccessToken ticket = data.token();
            if (ticket != null) {
                return ticket.Id;
            }
        }
        return null;
    }

    public static void signOut() {
        Principal data = Principal.current();
        if (data != null) {
            AccessToken ticket = data.token();
            if (ticket != null) {

                login(UMC.Security.Identity.create(ticket.Id, "?", ""), ticket.Id, ticket.ContentType);
            }
        }
    }

    public static AccessToken activation(String Username, AccessToken auth, String client) {
        switch (Username) {
            case "#":
            case "?":
                AccessToken Token = create(auth.identity(), auth.Id, client, auth.Timeout);
                UMC.Security.Membership.Instance().Activation(Token);
                return Token;
            default:
                Identity Id = UMC.Security.Membership.Instance().Identity(Username);
                AccessToken Token2 = create(Id, auth.Id, client, auth.Timeout);
                UMC.Security.Membership.Instance().Activation(Token2);
                return Token2;

        }

    }

    public AccessToken put(String key, String value) {
        if (Utility.isEmpty(key) == false) {
            if (Utility.isEmpty(value)) {
                this.Data.remove(key);
            } else {
                this.Data.put(key, value);
            }
        }
        return this;
    }

    public static AccessToken create(Identity Id, UUID tmpId, String contentType, int timeout) {
        AccessToken auth = new AccessToken();
        auth.ContentType = contentType;
        auth.Timeout = timeout;
        auth.Id = tmpId;
        auth.Username = Id.name();
        auth.SId = Id.id();
        auth.ActiveTime = new Date().getTime() / 1000;
        auth.Roles = null;

        switch (Id.name()) {
            case "#":
            case "?":
                if (Utility.isEmpty(Id.alias()) == false) {
                    auth.Data.put("#", Id.alias());
                }
                break;
            default:

                auth.Data.put("#", Id.alias());
                if (Id instanceof Identity.Identition) {
                    String[] roels = ((Identity.Identition) Id)._rolres;
                    if (roels != null) {
                        auth.Roles = String.join(",", roels);
                    }
                }
                break;
        }
        return auth;
    }

    public AccessToken put(Map<String, String> NameValue) {
        this.Data.putAll(NameValue);
        return this;
    }

    public void commit() {
        this.ActiveTime = new Date().getTime() / 1000;
        UMC.Security.Membership.Instance().Activation(this);
    }

    public UMC.Security.Identity identity() {


        long time = Utility.isNull(this.ActiveTime, 0) + this.Timeout;

        String Alias = this.Data.get("#");//["#"] as String ??String.Empty;
        if (this.Timeout > 0 && time <= System.currentTimeMillis()) {
            return UMC.Security.Identity.create(this.Id, "?", Alias);
        }
        if (Utility.isEmpty(this.Username)) {
            return UMC.Security.Identity.create(this.Id, "?", Alias);
        }
        switch (this.Username) {
            case "?":
                return UMC.Security.Identity.create(this.SId, "?", Alias);
            case "#":
                if (this.SId != null) {
                    return UMC.Security.Identity.create(this.SId, "#", Alias);
                } else {
                    return UMC.Security.Identity.create(this.Id, "?", Alias);
                }
            default:
                if (this.SId != null) {
                    if (Utility.isEmpty(this.Roles)) {
                        return UMC.Security.Identity.create(this.SId, this.Username, Alias);

                    } else {
                        return UMC.Security.Identity.create(this.SId, this.Username
                                , Alias, this.Roles.split(","));
                    }
                } else {
                    return UMC.Security.Identity.create(this.Id, "?", Alias);
                }
        }
    }

    public static AccessToken login(Identity Id, UUID tmpId, String client) {
        return login(Id, tmpId, 30, client);

    }

    public static AccessToken login(Identity Id, UUID tmpId, String contentType, boolean unqiue) {
        if (unqiue) {
            AccessToken token = create(Id, tmpId, contentType, 0);

            Configuration<AccessToken> sesion = new Configuration<>(token, token.Id.toString());

            sesion.ContentType = contentType;

            sesion.commit(Id, contentType);

            UMC.Security.Principal.create(Id, token);
            return token;
        } else {
            return login(Id, tmpId, 30, contentType);
        }

    }

    public static AccessToken login(Identity Id, UUID tmpId, int timeout, String contentType) {
        UMC.Security.Principal.create(Id);

        AccessToken auth = create(Id, tmpId, contentType, timeout);// new AccessToken();

        UMC.Security.Membership.Instance().Activation(auth);
        return auth;

    }

    public static String get(String key) {
        return AccessToken.current().Data.get(key);

    }

    public static void set(String key, String value) {
        AccessToken.current().put(key, value).commit();

    }

    public static AccessToken current()
    {

        Principal principal = Principal.current();//.get();
        return principal.token();
    }

    public static void set(Map<String, String> NameValue) {

        Principal principal = Principal.current();

        AccessToken ticket = principal.token();
        if (ticket == null) {
            return;
        }
        ticket.Data.putAll(NameValue);
        ;
        UMC.Security.Membership.Instance().Activation(ticket);

    }

}
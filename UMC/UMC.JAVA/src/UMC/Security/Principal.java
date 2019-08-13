package UMC.Security;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

public final class Principal {

    private static final ThreadLocal<Principal> threadLocal = new ThreadLocal<Principal>();


    private Principal(UUID appKey) {
        this._AppKey = appKey;
    }

    private Map _Items = new HashMap();

    public Map items() {
        return _Items;
    }

    public static Principal current()
    {

        return threadLocal.get();

    }

    public void set() {
        threadLocal.set(this);
    }

    public static Principal instance(UUID appKey, Identity user) {
        Principal p = new Principal(appKey);
        p._identity = user;
        threadLocal.set(p);
        return p;
    }

    public static Principal create(UUID appKey) {
        Principal princ = threadLocal.get();
        if (princ == null) {
            princ = new Principal(null);
            threadLocal.set(princ);

        }
        princ._AppKey = appKey;
        return princ;
    }

    public static Principal create(String root) {
        Principal princ = threadLocal.get();
        if (princ == null) {
            princ = new Principal(null);
            threadLocal.set(princ);

        }
        princ._Root = root;
        return princ;
    }


    public static Principal create(Identity identity) {
        Principal princ = threadLocal.get();
        if (princ == null) {
            princ = new Principal(null);
            threadLocal.set(princ);

        }
        princ._identity = identity;
        return princ;

    }

    private String _Root;

    public String root()

    {
        return _Root;
    }

    public static Principal create(Identity identity, AccessToken token) {
        Principal princ = threadLocal.get();

        if (princ == null) {
            princ = new Principal(null);
            threadLocal.set(princ);

        }
        princ._identity = identity;
        princ._Token = token;
        return princ;
    }

    Identity _identity;
    UUID _AppKey;

    /// <summary>
    ///平台线程身份标识
    /// </summary>
    public UUID appKey()
    {
        return this._AppKey;
    }

    private  AccessToken _Token;

    /// <summary>
    ///  身份数据
    /// </summary>
    public  AccessToken token()
    {
        return _Token;
    }


    public Identity identity() {

        return _identity;

    }


}
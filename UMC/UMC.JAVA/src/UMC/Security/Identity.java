package UMC.Security;

import UMC.Data.Utility;

import java.util.UUID;
import java.util.stream.Stream;

public abstract class Identity {

     static class Identition extends Identity {

        Identition(UUID sn, String alias, String name, String... roels) {
            this._id = sn;
            this._Alias = alias;
            this._Name = name;
            this._rolres = roels;
        }

        UUID _id;
        String _Alias;

        @Override
        public String alias() {

            return _Alias;

        }

        @Override
        public boolean isInRole(String role) {
            if (_rolres != null) {
                for (int r = 0; r < _rolres.length; r++) {
                    String s = _rolres[r];
                    if (s.equals(Membership.AdminRole)) {
                        return true;
                    }
                    if (s.equalsIgnoreCase(role)) {
                        return true;
                    }
                }
            }
            return false;
        }

        String[] _rolres;
        String _Name;

        @Override
        public UUID id() {
            return _id;
        }

        @Override
        public String name() {
            return this._Name;
        }


    }


    public static Identity current()

    {

        Principal principal = Principal.current();//.get();
        if (principal != null)
            return principal.identity();
        return null;

    }

    public boolean isAuthenticated() {

        if (!Utility.isEmpty(this.name()) && this.name().equals("?") == false) {
            return true;
        } else {
            UUID t = AccessToken.token();
            if (t != null) {
                if (t.compareTo(id()) != 0) {
                    return true;
                }
            }

            return false;
        }

    }

    public abstract UUID id();

    public abstract String name();

    public abstract String alias();

    public abstract boolean isInRole(String role);


    public static Identity create(String name) {
        return new Identition(null, null, name);
    }

    public static Identity create(String name, String alias) {
        return new Identition(null, alias, name);
    }

    public static Identity create(UUID sn, String name, String alias, String... roels) {
        return new Identition(sn, alias, name, roels);
    }
}

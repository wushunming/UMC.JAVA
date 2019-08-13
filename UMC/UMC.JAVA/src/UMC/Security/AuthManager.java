package UMC.Security;


import UMC.Data.Database;
import UMC.Data.Entities.Wildcard;
import UMC.Data.Sql.IObjectEntity;
import UMC.Data.Utility;

import java.util.*;

/**
 * 授权管理
 */
public class AuthManager {

    public final static String UserAllow = "UserAllow",
            UserDeny = "UserDeny",
            RoleAllow = "RoleAllow",
            RoleDeny = "RoleDeny";

    public static class Authorize {
        public String Type;

        public String Value;
    }

    Identity principal;

    public AuthManager(Identity user) {
        this.principal = user;
    }

    public static boolean authorization(String wildcard) {

        return authorization(Identity.current(), wildcard)[0];
    }

    public static boolean[] authorization(String... wildcards) {
        return authorization(Identity.current(), wildcards);
    }

    public static boolean[] authorization(Identity princ, String... wildcards) {
        boolean[] rerValue = new boolean[wildcards.length];
        if (wildcards.length > 0) {
            if (princ.isInRole(UMC.Security.Membership.AdminRole)) {
                for (int i = 0; i < rerValue.length; i++) {
                    rerValue[i] = true;
                }
                return rerValue;
            }
            List<String> list = new LinkedList<>();// L List<String>();
            for (String wildcard : wildcards) {
                list.add(wildcard);
                int l = wildcard.length() - 1;

                while (l > -1) {
                    switch (wildcard.charAt(l)) {
                        case '.':
                            list.add(wildcard.substring(0, l) + ".*");
                            break;
                    }
                    l--;
                }
            }
            AuthManager wMger = new AuthManager(princ);
            Integer[] vs = wMger.check(list.toArray(new String[0]));
            int start = 0, end = 0;

            for (int i = 1; i < wildcards.length; i++) {
                final int index = i;
                end = Utility.findIndex(list, w -> wildcards[index].equalsIgnoreCase(w));

                rerValue[i - 1] = authorization(vs, start, end);
                start = end;


            }
            rerValue[wildcards.length - 1] = authorization(vs, start, vs.length);
        }
        return rerValue;
    }

    static boolean authorization(Integer[] vs, int start, int end) {
        for (int c = start; c < end; c++) {
            if (vs[c] != 0) {
                return vs[c] > 0;
            }
        }

        return true;
    }

    int check(Authorize[] Authorizes) {
        int isAllowRoles = 0;
        int isAllowUser = 0;
        String username = this.principal.name();

        for (Authorize dr : Authorizes) {

            String sValue = dr.Value;
            switch (dr.Type) {
                case UserAllow:
                    switch (sValue) {
                        case "*":
                            if (isAllowUser == 0) {
                                isAllowUser = 1;
                            }
                            ;
                            break;
                        case "?":
                            if (username.equalsIgnoreCase("?") == false) {
                                isAllowUser = 1;
                            }
                            break;
                        default:
                            if (username.equalsIgnoreCase(sValue)) {
                                isAllowUser = 1;
                            }
                            break;
                    }
                    break;
                case RoleAllow:
                    if (sValue.equalsIgnoreCase("*")) {
                        if (isAllowRoles == 0) {
                            isAllowRoles = 1;
                        }
                    } else if (isAllowRoles != 1) {
                        if (principal.isInRole(sValue)) {
                            isAllowRoles = 1;
                        } else {
                            isAllowRoles = -1;
                        }
                    }
                    break;
                case UserDeny:
                    if (isAllowUser > -1) {
                        switch (sValue) {
                            case "*":
                                if (isAllowUser == 0) {
                                    isAllowUser = -1;
                                }
                                ;
                                break;
                            case "?":
                                if (username.equalsIgnoreCase("?") == false) {
                                    isAllowUser = -1;
                                }
                                break;
                            default:
                                if (username.equalsIgnoreCase(sValue)) {
                                    isAllowUser = -1;
                                }
                                break;
                        }
                    }
                    break;
                case RoleDeny:
                    if (sValue == "*") {
                        if (isAllowRoles == 0) {
                            isAllowRoles = -1;
                        }
                    } else if (isAllowRoles != -1 && principal.isInRole(sValue)) {
                        isAllowRoles = -1;

                    }
                    break;
            }
        }
        if (isAllowUser == 0) {
            return isAllowRoles;
        }
        return isAllowUser;
    }

    protected void delete(String wildcard) {

        Database.instance().objectEntity(Wildcard.class).where().and().equal("WildcardKey", wildcard)
                .entities().delete();

    }

    protected boolean copy(String sourceKey, String destKey) {
        IObjectEntity<Wildcard> entityWDKWildcards = UMC.Data.Database.instance().objectEntity(Wildcard.class);
        //< Wildcard > ();

        entityWDKWildcards.where().and().equal("WildcardKey", sourceKey);

        Wildcard wildcard = entityWDKWildcards.single();
        if (wildcard != null) {
            wildcard.WildcardKey = destKey;
            entityWDKWildcards.where().reset().and().equal("WildcardKey", destKey);
            if (entityWDKWildcards.count() == 0) {
                entityWDKWildcards.insert(wildcard);
            }
        }

        return true;
    }

    protected Integer[] check(String... wildcards) {

        List<Integer> vs = new LinkedList<>();
        if (wildcards.length > 0) {

            Map<String, Wildcard> lis = new LinkedHashMap<>();
            Database.instance().objectEntity(Wildcard.class)
                    .where().and().in("WildcardKey", wildcards).entities().query(d -> lis.put(d.WildcardKey.toLowerCase(), d));

            for (String w : wildcards) {
                Wildcard au = lis.get(w.toLowerCase());
                if (au != null) {
                    if (Utility.isEmpty(au.Authorizes)) {
                        vs.add(0);
                    } else {
                        vs.add(check(UMC.Data.JSON.deserialize(au.Authorizes, Authorize[].class)));

                    }
                } else {
                    vs.add(0);
                }
            }
        }
        return vs.toArray(new Integer[]{0});

    }
}
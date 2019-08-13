package UMC.Security;


import UMC.Data.Entities.Account;
import UMC.Data.Entities.Role;
import UMC.Data.Entities.UserToRole;
import UMC.Data.Utility;
import UMC.Data.*;
import UMC.Data.Sql.IObjectEntity;
import UMC.Data.Sql.Script;

import java.util.*;

public class Membership extends UMC.Data.DataProvider {
    public final static String SessionCookieName = ".WDKTemporary";


    /**
     * Email账户
     */
    public final static int EMAIL_ACCOUNT_KEY = 1,
    /**
     * 移动电话
     */
    MOBILE_ACCOUNT_KEY = 2,
    /**
     * 手机MAC地址
     */
    MAC_ACCOUNT_KEY = 5;
    /**
     * 正常
     */
    public static final int UserFlagsNormal = 0,

    /**
     * 锁定
     */
    UserFlagsLock = 1,
    /**
     * 要更新密码
     */
    UserFlagsChanging = 2,

    /**
     * 不能更新密码
     */
    UserFlagsUnChangePasswork = 4,

    /**
     * 没有通过验证
     */
    UserFlagsUnVerification = 8,
    /**
     * 禁用
     */
    UserFlagsDisabled = 16;


    /**
     *管理员角色
     */
    public static final String AdminRole = "Administrators",

    /**
     *用户角色
     */
    UserRole = "Users",

    /**
     *来宾角色
     */
     GuestRole = "Guest";
    private static Membership membership;

    public static Membership Instance() {
        if (membership == null) {
            membership = (Membership) Utility.createObject("Membership");

        }
        if (membership == null) {
            membership = new Membership();
        }
        return membership;
    }

    public UMC.Security.Principal Authorization(UUID SessionKey, String contentType) {

        Configuration<AccessToken> session = new Configuration<AccessToken>(SessionKey.toString(), AccessToken.class);
        if (session.Value != null) {
            AccessToken auth = session.Value;
            auth.Id = SessionKey;
            auth.ContentType = session.ContentType;
            return Authorization(auth, auth.identity());

        }
        Identity user = Identity.create(SessionKey, "?", "");
        return Principal.create(user, AccessToken.create(user, SessionKey, contentType, 0));


    }

    public UMC.Security.Principal Authorization(AccessToken auth, UMC.Security.Identity id) {

        long cTime = System.currentTimeMillis() / 1000;
        long ative = Utility.isNull(auth.ActiveTime, 0l);

        if (ative < cTime - 600) {
            auth.ActiveTime = cTime;

            this.Activation(auth);
        }
        return Principal.create(id, auth);
    }

    public static class User extends UMC.Data.Entities.User {
        public String Password;
    }

    /** 比对密码
     * @param username 用户名
     * @param password 密码
     * @param max 最大比对次数
     * @return
     */
    public int Password(String username, String password, int max) {
        if (UMC.Data.Utility.isEmpty(username)) throw new IllegalArgumentException("username");

        IObjectEntity<User> userEntiy = Database.instance().objectEntity(User.class);
        userEntiy.where().and().equal("Username", username);

        User user = userEntiy.single();
        user.Flags = Utility.isNull(user.Flags, 0);
        if (user == null) {
            return -1;
        } else {
            if ((user.Flags & UserFlagsLock) == UserFlagsLock) {
                return -2;
            } else if ((user.Flags & UserFlagsDisabled) == UserFlagsDisabled) {
                return -3;
            } else if (UMC.Data.Utility.isEmpty(user.Password)) {
                return 0;
            }


            String destPwd = Utility.des(Base64.getDecoder().decode(user.Password), user.Id);
            if (destPwd == null) {
                return -1;
            }

            int spIndex = password.indexOf(':');
            if (spIndex > 0) {
                String md5pwd = password.substring(spIndex + 1);
                String sp = password.substring(0, spIndex);
                if (md5pwd.length() == 32) {
                    password = password.substring(spIndex + 1);
                    destPwd = UMC.Data.Utility.md5(sp + destPwd);
                }
            }
            if (destPwd.equalsIgnoreCase(password)) {
                User user1 = new User();
                user1.VerifyTimes = 0;
                user1.ActiveTime = new Date();
                userEntiy.update(user1);
                return 0;
            } else if (max > 0) {
                User s = new User();

                s.VerifyTimes = Utility.isNull(user.VerifyTimes, 0) + 1;
                if (max <= user.VerifyTimes) {
                    s.Flags = Utility.isNull(user.Flags, 0) | UserFlagsLock;
                    userEntiy.update(s);
                } else {
                    userEntiy.update(s);
                }
                return s.VerifyTimes;
            } else {
                return 1;
            }
        }
    }

    /**
     * @param name
     * @param accountType
     * @return
     */
    public Identity Identity(String name, int accountType) {
        Account awh = new Account();
        awh.Name = name;
        awh.Type = accountType;
        Account acount = Database.instance().objectEntity(Account.class)
                .where().and(awh).entities().single();
        if (acount == null) {
            return null;
        }

        Identity user = this.Identity(acount.user_id);
        if (user == null) {
            Object object = JSON.deserialize(acount.ConfigData);
            String Alias = "关联用户";
            if (object instanceof Map) {
                String str = (String) ((Map) object).get("Alias");
                if (UMC.Data.Utility.isEmpty(str) == false) {
                    Alias = str;
                }

            }
            return Identity.create(acount.user_id, "#", Alias);
        }
        return user;
    }

    public boolean Password(String Username, String password) {
        if (UMC.Data.Utility.isEmpty(Username)) throw new IllegalArgumentException("username");

        IObjectEntity<User> userEntiy = Database.instance().objectEntity(User.class);

        userEntiy.where().and().equal("Username", Username);

        User user = userEntiy.single();

        if (user != null) {
            User user2 = new User();

            user2.Password = new String(Base64.getEncoder().encode(Utility.des(password, user.Id)));

            return userEntiy.update(user2) > 0;

        }
        return false;

    }


    public boolean DeleteUser(String username) {
        if (UMC.Data.Utility.isEmpty(username)) throw new IllegalArgumentException("username");
        IObjectEntity<User> userEntiy = Database.instance().objectEntity(User.class);

        userEntiy.where().and().equal("Username", username);
        User user = userEntiy.single();
        if (user != null) {
            IObjectEntity<Role> WDKRole = Database.instance().objectEntity(Role.class);
            WDKRole.where().and().equal("Rolename", AdminRole);

            Role role = WDKRole.single();
            if (role != null) {
                IObjectEntity<UserToRole> WDKUserToRole = Database.instance().objectEntity(UserToRole.class);

                WDKUserToRole.where().and().equal("role_id", role.Id);
                if (WDKUserToRole.count() == 1) {
                    return false;
                }
                WDKUserToRole.where().reset()
                        .and().equal("user_id", user.Id);

                WDKUserToRole.delete();
                Database.instance().objectEntity(Account.class)
                        .where().and().equal("user_id", user.Id).entities().delete();


            }
        }
        userEntiy.delete();

        return true;
    }


    public UUID CreateUser(String username, String password, String alias) {
        if (UMC.Data.Utility.isEmpty(username)) throw new IllegalArgumentException("username");
        UUID sn = NewSN();
        CreateUser(sn, username, password, alias);//.id();
        return sn;
    }

    public UUID NewSN() {
        return UUID.randomUUID();
    }

    public boolean ChangeFlags(String username, int flags) {
        if (UMC.Data.Utility.isEmpty(username)) throw new IllegalArgumentException("username");
        IObjectEntity<User> userEntiy = Database.instance().objectEntity(User.class);

        userEntiy.where().and().equal("Username", username);
        User user = new User();
        user.Flags = flags;

        return userEntiy.update(user) > 0;


    }

    public boolean Exists(String Username) {
        if (UMC.Data.Utility.isEmpty(Username)) throw new IllegalArgumentException("username");

        return Database.instance().objectEntity(User.class)
                .where().and().equal("Username", Username)
                .entities().count() > 0;

    }

    public Identity Identity(String username) {
        if (UMC.Data.Utility.isEmpty(username)) throw new IllegalArgumentException("username");
        IObjectEntity<User> userEntiy = Database.instance().objectEntity(User.class);

        userEntiy.where().and().equal("Username", username);
        User user = userEntiy.single();

        if (user == null) {
            return null;
        }
        int flags = user.Flags;//??Security.UserFlags.Normal;
        if ((flags & UserFlagsLock) == UserFlagsLock) {
            return UMC.Security.Identity.create(user.Id, user.Username, user.Alias);
        }
        List<String> roles = new LinkedList<>();
        Script sqlScript = Database.instance().objectEntity(UserToRole.class) // < UMC.Data.Entities.UserToRole > ()
                .where().and().equal("user_id", user.Id)
                .entities().script("role_id");


        Database.instance().objectEntity(Role.class)
                .where().and().in("Id", sqlScript)
                .entities().query((dr) -> roles.add(dr.Rolename));

        ;
        return UMC.Security.Identity.create(user.Id, user.Username, user.Alias, roles.toArray(new String[]{}));

    }


    public boolean RemoveRole(String Username, String rolename) {
        if (UMC.Data.Utility.isEmpty(Username)) throw new IllegalArgumentException("username");

        IObjectEntity<User> userEntiy = Database.instance().objectEntity(User.class);

        userEntiy.where().and().equal("Username", Username);

        User user = userEntiy.single();

        if (user == null) {
            return false;
        }
        IObjectEntity<Role> roleEntiy = Database.instance().objectEntity(Role.class);

        roleEntiy.where().and().equal("Rolename", rolename);
        Role role = roleEntiy.single();
        if (role != null) {

            IObjectEntity<UserToRole> UserToRoleEntiy = Database.instance().objectEntity(UserToRole.class);


            UserToRoleEntiy.where().and().equal("role_id", role.Id);

            if (rolename.equalsIgnoreCase(AdminRole)) {
                if (UserToRoleEntiy.count() == 1) {
                    return false;
                } else {
                    UserToRoleEntiy.where().and().equal("user_id", user.Id);
                }
            } else {

                UserToRoleEntiy.where().and().equal("user_id", user.Id);


            }
            UserToRoleEntiy.delete();
            return true;
        }

        return false;
    }

    public boolean AddRole(String Username, String... roles) {
        if (UMC.Data.Utility.isEmpty(Username)) throw new IllegalArgumentException("username");
        if (roles.length > 0) {

            IObjectEntity<User> userEntiy = Database.instance().objectEntity(User.class);

            userEntiy.where().and().equal("Username", Username);

            User user = userEntiy.single();

            if (user == null) {
                return false;
            }

            if (user != null) {
                IObjectEntity<Role> roleEntiy = Database.instance().objectEntity(Role.class);
                roleEntiy.where().and().in("Rolename", Arrays.asList(roles).toArray());


                List<UUID> ids = new LinkedList<>();
                List<UserToRole> tds = new LinkedList<>();
                roleEntiy.query((dr) ->
                {
                    ids.add(dr.Id);
                    UserToRole ur = new UserToRole();
                    ur.role_id = dr.Id;
                    ur.user_id = user.Id;
                    tds.add(ur);
                });
                if (ids.size() > 0) {
                    IObjectEntity<UserToRole> UserToRoleEntiy = Database.instance().objectEntity(UserToRole.class);

                    UserToRoleEntiy.where().and().equal("user_id", user.Id).and().in("role_id", ids.toArray());


                    UserToRoleEntiy.delete();
                    UserToRoleEntiy.insert(tds.toArray(new UserToRole[]{}));

                    return true;
                }
            }
        }

        return false;
    }

    public boolean ChangeAlias(String username, String alias) {

        if (UMC.Data.Utility.isEmpty(username)) throw new IllegalArgumentException("username");
        User user = new User();
        user.Alias = alias;
        IObjectEntity<User> userEntiy = Database.instance().objectEntity(User.class);

        userEntiy.where().and().equal("Username", username);
        return userEntiy.update(user) > 0;

    }

    public void Activation(AccessToken token) {
        Configuration<AccessToken> sesion = new Configuration(token, token.Id.toString());
        sesion.ContentType = token.ContentType;
//        sesion.Value = token;

        switch (token.Username) {
            case "?":
                if (token.SId != null) {
                    sesion.commit(token.SId);
                } else {
                    sesion.commit(token.Id);
                }
                break;
            case "#":
                sesion.commit(token.identity().id());
                break;
            default:
                sesion.commit(token.identity().id());
                User user = new User();
                user.ActiveTime = new Date();
                user.SessionKey = token.Id;

                UMC.Data.Database.instance().objectEntity(User.class)
                        .where().and().equal("Username", token.Username)
                        .entities().update(user);


                break;
        }

    }

    public String Password(String username) {
        if (UMC.Data.Utility.isEmpty(username)) throw new IllegalArgumentException("username");
        IObjectEntity<User> userEntiy = Database.instance().objectEntity(User.class);

        userEntiy.where().and().equal("Username", username);
        User user = userEntiy.single();


        if (user != null) {

            return Utility.des(Base64.getDecoder().decode(user.Password), user.Id);
        }
        return null;
    }

    public Identity Identity(UUID id) {

        IObjectEntity<User> userEntiy = Database.instance().objectEntity(User.class);

        userEntiy.where().and().equal("Id", id);
        User user = userEntiy.single();

        if (user == null) {
            return null;
        }
        int flags = user.Flags;//??Security.UserFlags.Normal;
        if ((flags & UserFlagsLock) == UserFlagsLock) {
            return UMC.Security.Identity.create(user.Id, user.Username, user.Alias);
        }
        List<String> roles = new LinkedList<>();
        Script sqlScript = Database.instance().objectEntity(UserToRole.class) // < UMC.Data.Entities.UserToRole > ()
                .where().and().equal("user_id", user.Id)
                .entities().script("role_id");


        Database.instance().objectEntity(Role.class)
                .where().and().in("Id", sqlScript)
                .entities().query((dr) -> roles.add(dr.Rolename));

        String[] ts = new String[]{};
        roles.toArray(ts);
        return UMC.Security.Identity.create(user.Id, user.Username, user.Alias, ts);

    }

    public Identity CreateUser(UUID id, String username, String password, String alias) {

        if (UMC.Data.Utility.isEmpty(username)) throw new IllegalArgumentException("username");
        IObjectEntity<User> userEntiy = Database.instance().objectEntity(User.class);

        userEntiy.where().and().equal("Username", username);

        if (userEntiy.count() == 0) {
            UUID sn = id;// UUID.randomUUID();
            User user = new User();
            user.Flags = UserFlagsNormal;
            user.Id = sn;
            user.RegistrTime = new Date();
            user.Username = username;
            user.Password = Base64.getEncoder().encodeToString(Utility.des(password, sn));
            userEntiy.insert(user);

            return Identity.create(id, username, alias);
        }

        return null;

    }

    public static void Relation(Account main, Account... relations) {
        if (Utility.isEmpty(main.Name) || Utility.isNull(main.Type, 0) == 0) {
            throw new IllegalArgumentException("main 中的属性name和Type必须有值");
        }
        List<String> names = new LinkedList<>();
        names.add(main.Name);
        List<Integer> types = new LinkedList<>();

        types.add(main.Type);
        for (Account r : relations) {
            if (Utility.isNull(r.Type, 0) != 0)
                types.add(r.Type);
            if (Utility.isEmpty(r.Name) == false) {
                names.add(r.Name);
            }
        }
        IObjectEntity<Account> entity = UMC.Data.Database.instance().objectEntity(Account.class);


        entity.where().and().in("Name"
                , names.toArray()).and().in("Type", types.toArray());

        List<UUID> mids = new LinkedList<>();
        final UUID[] mid = {UUID.randomUUID()};// Guid.NewGuid();
        List<Account> rels = new LinkedList<>();

        entity.order().asc("user_id");
        entity.query(g ->
        {
            rels.add(g);
            if (mid[0].compareTo(g.user_id) != 0) {
                mid[0] = g.user_id;
                mids.add(mid[0]);
            }

        });

        if (mids.size() > 0) {
            entity.where().reset().and().in("user_id"
                    , mids.toArray()).and().in("Type", types.toArray());
            entity.query(g -> rels.add(g));
        }
        UUID memberId = null;
        List<Account> orel = Utility.findAll(rels, g -> g.Type == main.Type);///   rels.FindAll(g = > g.Type == main.Type);


        Account om = Utility.find(orel, g -> main.Name.equals(g.Name) && g.Type == main.Type);
        if (om == null) {
            if (relations.length > 0) {
                Account rel = Utility.find(Arrays.asList(relations), g -> Utility.isEmpty(g.Name) == false);
                if (rel != null) {

                    Account v = Utility.find(rels, g -> g.Name.equals(rel.Name) && rel.Type == g.Type);
                    if (v != null) {
                        main.user_id = v.user_id;
                    }
                }
            }
            if (main.user_id != null) {
                main.user_id = UUID.randomUUID();
            }
            main.Flags = Membership.UserFlagsNormal;

            Account wacc = new Account();
            wacc.Type = main.Type;
            wacc.user_id = main.user_id;
            entity.where().reset().and().equal(wacc);
            if (entity.update(main) == 0) {
                entity.insert(main);
            }
        } else {
            if (main.user_id != null) {
                if (main.user_id.compareTo(om.user_id) == 0) {
                    Account wacc = new Account();
                    wacc.Type = main.Type;
                    wacc.user_id = main.user_id;

                    entity.where().reset().and().equal(wacc)
                            .entities().iff(e -> e.update(main) == 0, e -> e.insert(main));

                }
            } else {
                main.user_id = om.user_id;
            }
        }
        memberId = main.user_id;
        for (Account r : relations) {
            UUID finalMemberId = memberId;
            List<Account> mrel = Utility.findAll(rels, g -> g.Type == r.Type && finalMemberId == g.user_id);


            if (Utility.isEmpty(r.Name) == false) {


                List<Account> mcards = Utility.findAll(rels, g -> g.Name.equalsIgnoreCase(r.Name));


                switch (mcards.size()) {
                    case 0:
                        r.user_id = memberId;

                        if (mrel.size() > 0) {
                            mids.add(memberId);
                            entity.where().reset().and().equal("Type", r.Type)
                                    .and().in("user_id", mids.toArray())
                                    .entities().delete();
                        }
                        entity.insert(r);
                        mrel.add(r);
                        break;
                    default:

                        r.Flags = Membership.UserFlagsNormal;
                        r.user_id = mcards.get(0).user_id;
                        r.Name = mcards.get(0).Name;
                        r.user_id = mcards.get(0).user_id;
                        r.ConfigData = mcards.get(0).ConfigData;
                        break;
                }
            } else {

                UUID finalMemberId1 = memberId;
                Account m = Utility.find(rels, g -> g.Type == r.Type && finalMemberId1 == g.user_id);

                if (m != null) {
                    r.user_id = m.user_id;
                    r.Name = m.Name;
                    r.Flags = m.Flags;
                    r.ConfigData = m.ConfigData;
                }
            }

        }

    }

}

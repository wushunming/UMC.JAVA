package UMC.Activities;


import UMC.Data.Entities.Role;
import UMC.Data.Entities.Wildcard;
import UMC.Data.JSON;
import UMC.Data.Sql.IObjectEntity;
import UMC.Data.Utility;
import UMC.Security.AuthManager;
import UMC.Web.*;

import java.util.*;


@Mapping(model = "Settings", cmd = "Auth", auth = WebAuthType.admin, desc = "功能授权")
public class SettingsAuthActivity extends WebActivity {
    @Override
    public void processActivity(WebRequest request, WebResponse response) {


        IObjectEntity<Role> roleEntity = UMC.Data.Database.instance().objectEntity(Role.class);
        String RoleType = this.asyncDialog("Type", d ->
        {
            if (roleEntity.count() < 4) {
                return UIDialog.returnValue("User");
            }
            UIRadioDialog rd = new UIRadioDialog();
            rd.title("选择设置账户类型");
            rd.options().add("角色", "Role");
            rd.options().add("用户", "User");
            return rd;
        });

        String setValue = this.asyncDialog("Value", d ->
        {
            if (RoleType.equalsIgnoreCase("role")) {

                UIRadioDialog rd = new UIRadioDialog();
                rd.title("请选择设置权限的角色");
                roleEntity.where().reset().and().notIn("Rolename",
                        UMC.Security.Membership.GuestRole
                        , UMC.Security.Membership.AdminRole);

                roleEntity.query(dr -> rd.options().add(dr.Rolename, dr.Rolename));
                return rd;
            } else {
                UserDialog userDialog = new UserDialog();
                userDialog.title("请选择设置权限的账户");
                return userDialog;

            }
        });
        List<WebMeta> configuration = WebServlet.auths();

        if (configuration.size() == 0) {
            this.prompt("现在的功能不需要设置权限");
        }
        List<String> wdcks = new LinkedList<>();
        for (int i = 0, l = configuration.size(); i < l; i++) {
            wdcks.add(configuration.get(i).get("key"));
        }
        List<Wildcard> wdks = new LinkedList<>();

        IObjectEntity<Wildcard> wddEntity = UMC.Data.Database.instance().objectEntity(Wildcard.class);
        wddEntity.where().and().in("WildcardKey", wdcks.toArray())
                .entities().query(dr -> wdks.add(dr));


        String WildcardKey = this.asyncDialog("Wildcards", d ->
        {
            UICheckboxDialog fmdg = new UICheckboxDialog("None");
            fmdg.title("功能权限设置");


            for (int i = 0, l = configuration.size(); i < l; i++) {

                WebMeta provider = configuration.get(i);
                String id = provider.get("key");

                Wildcard wdk = Utility.find(wdks, w -> id.equalsIgnoreCase(w.WildcardKey));

                if (wdk != null) {
                    Object[] config = (Object[]) JSON.deserialize(wdk.Authorizes);
                    if (config != null) {
                        boolean isS = false;
                        if (RoleType.equalsIgnoreCase("Role")) {
                            isS = Utility.exists(config, cd -> {
                                Map map = (Map) cd;
                                return map.get("Type").toString().equalsIgnoreCase("RoleDeny")
                                        && map.get("Value").toString().equalsIgnoreCase(setValue);
                            });

                        } else {
                            isS = Utility.exists(config, cd -> {
                                Map map = (Map) cd;
                                return map.get("Type").toString().equalsIgnoreCase("UserDeny")
                                        && map.get("Value").toString().equalsIgnoreCase(setValue);
                            });
                        }
                        fmdg.options().add(provider.get("desc"), id, !isS);
                    } else {
                        fmdg.options().add(provider.get("desc"), id, true);
                    }
                } else {
                    fmdg.options().add(provider.get("desc"), id, true);
                }
            }

            return fmdg;

        });

        for (int i = 0, l = configuration.size(); i < l; i++) {
            WebMeta provider = configuration.get(i);
            String id = provider.get("key");

            Wildcard wdk = Utility.find(wdks, w -> id.equalsIgnoreCase(w.WildcardKey));
            List<Object> authorizes = new LinkedList<>();

            if (wdk != null) {
                Object[] config = (Object[]) JSON.deserialize(wdk.Authorizes);

                authorizes.addAll(Arrays.asList(config));
            }
            if (RoleType.equalsIgnoreCase("Role")) {
                authorizes.removeAll(
                        Utility.findAll(authorizes, a -> {

                            Map map = (Map) a;
                            String Type = map.get("Type").toString();
                            return (Type.equalsIgnoreCase(AuthManager.RoleDeny) ||
                                    Type.equalsIgnoreCase(AuthManager.RoleAllow))
                                    && map.get("Value").toString().equalsIgnoreCase(setValue);

                        })
                );
            } else {
                authorizes.removeAll(
                        Utility.findAll(authorizes, a -> {

                            Map map = (Map) a;
                            String Type = map.get("Type").toString();
                            return (Type.equalsIgnoreCase(AuthManager.UserAllow) ||
                                    Type.equalsIgnoreCase(AuthManager.UserDeny))
                                    && map.get("Value").toString().equalsIgnoreCase(setValue);

                        })
                );
            }
            if (WildcardKey.indexOf(id) == -1) {

                if (RoleType.equalsIgnoreCase("Role")) {
                    Map map = new HashMap();
                    map.put("Value", setValue);
                    map.put("Type", AuthManager.RoleDeny);
                    authorizes.add(map);

                } else {
                    Map map = new HashMap();
                    map.put("Value", setValue);
                    map.put("Type", AuthManager.UserDeny);
                    authorizes.add(map);

                }
            }

            Wildcard newWdd = new Wildcard();
            newWdd.Authorizes = JSON.serialize(authorizes);
            newWdd.WildcardKey = id;
            newWdd.Description = provider.get("desc");

            wddEntity.where().reset().and().equal(new Wildcard().setWildcardKey(id))

                    .entities().iff(e -> e.update(newWdd) == 0, e -> e.insert(newWdd));

        }
        this.prompt("设置成功");

    }


}
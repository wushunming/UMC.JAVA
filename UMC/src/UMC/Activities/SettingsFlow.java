package UMC.Activities;

import UMC.Web.Mapping;
import UMC.Web.WebActivity;
import UMC.Web.WebAuthType;
import UMC.Web.WebFlow;

@Mapping(model = "Settings", auth = WebAuthType.admin)
public class SettingsFlow extends WebFlow {
    @Override
    public WebActivity firstActivity() {
        switch (this.context().request().cmd()) {
            case "Role":
                return new SettingsRoleActivity();
            case "User":
                return new SettingsUserActivity();
        }

        return WebActivity.Empty;
    }
}

package UMC.Activities;

import UMC.Web.Mapping;
import UMC.Web.WebActivity;
import UMC.Web.WebAuthType;
import UMC.Web.WebFlow;

@Mapping(model = "Design", auth = WebAuthType.user)
public class DesignFlow extends WebFlow {
    @Override
    public WebActivity firstActivity() {
        switch (this.context().request().cmd()) {
            case "Item":
                return new DesignItemActivity();
            case "Click":
                return new DesignClickActivity();
            case "Custom":
                return new DesignCustomActivity();
            case "Items":
                return new DesignItemsActivity();
            case "Image":
                return new DesignImageActivity();
            case "Home":
                return new DesignUIActivity(false);
            case "UI":
                return WebActivity.Empty;
            default:
                if (this.context().request().cmd().startsWith("UI")) {
                    return new DesignConfigActivity();
                }
                return WebActivity.Empty;
        }

    }
}


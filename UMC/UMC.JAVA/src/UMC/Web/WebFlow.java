package UMC.Web;

public abstract class WebFlow extends WebHandler {

    public static WebFlow Empty = new WebFlow() {
        @Override
        public WebActivity firstActivity() {
            return WebActivity.Empty;
        }
    };

    public abstract WebActivity firstActivity();

    public WebActivity nextActivity(String activityId) {
        return WebActivity.Empty;
    }

}
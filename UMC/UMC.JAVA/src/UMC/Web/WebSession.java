package UMC.Web;

import UMC.Data.DataProvider;
import UMC.Data.JSON;
import UMC.Data.Utility;
import UMC.Security.AccessToken;
import UMC.Security.Identity;

import java.lang.annotation.Annotation;
import java.util.Map;


public abstract class WebSession extends DataProvider {
    private static class POSSessioner extends WebSession {

        @Override
        public String header() {
            return AccessToken.get("WebSession");
        }

        @Override
        public void storage(Map header, WebContext context) {
            AccessToken.set("WebSession", JSON.serialize(header));
        }


    }

    public static WebSession Instance() {

        WebSession posSession = (WebSession) Utility.createObject("WebSession");
        if (posSession == null) {
            posSession = new POSSessioner();
        }
        return posSession;
    }

    public abstract String header();

    public abstract void storage(Map header, WebContext context);

    public boolean authorization(String model, String command) {
        return false;
    }

    protected WebRequest request() {
        return new WebRequest();
    }

    protected WebResponse response() {
        return new WebResponse();
    }

    protected WebContext context() {
        return new WebContext();
    }

    protected Map<String, Object> outer(WebClient client, WebContext context) {
        return null;

    }

    protected void check(WebContext context) {

    }

}

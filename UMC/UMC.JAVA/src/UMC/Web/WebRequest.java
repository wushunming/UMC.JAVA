package UMC.Web;

import UMC.Data.Utility;
import UMC.Security.Identity;
import UMC.Security.Membership;

import java.net.URI;
import java.util.Map;

public class WebRequest {
    private WebClient client;
    private boolean isMaster, isWeiXin;

    protected void init(Map header, WebClient client) {
        this.client = client;
        if (Utility.isEmpty(client.UserAgent) == false) {
            isWeiXin = client.UserAgent.indexOf("MicroMessenger") > 10;
        }
        isMaster = Identity.current().isInRole(Membership.AdminRole);

        _model = (String) header.get("POS-MODEL");
        _cmd = (String) header.get("POS-COMMAND");
        header.remove("POS-MODEL");
        header.remove("POS-COMMAND");

        WebMeta he = new WebMeta(header);

        this._Headers = he;
        this.Arguments = this._Headers.meta(KEY_HEADER_ARGUMENTS);
        if (this.Arguments == null) {
            this.Arguments = new WebMeta();
        }


        this._items = this.Arguments.meta(KEY_ARGUMENTS_ITEMS);

        if (this._items == null) {
            this._items = new WebMeta();
        }

        this.Arguments.remove(KEY_ARGUMENTS_ITEMS);
    }

    String _model, _cmd;

    public String model() {
        return _model;
    }

    /// <summary>
    /// 提交的值
    /// </summary>
    public String sendValue() {
        return this._Headers.get(this._cmd);

    }

    /// <summary>
    /// 提交的值
    /// </summary>
    public WebMeta sendValues() {

        return _Headers.meta(this._model);

    }

    public String userHostAddress() {
        return this.client.UserHostAddress;
    }

    WebMeta Arguments = new WebMeta();

    public WebMeta arguments() {
        return Arguments;
    }

    WebMeta _Headers;// = new WebMeta();

    public WebMeta headers() {
        return _Headers;
    }

    WebMeta _items;//= new WebMeta();

    public WebMeta items() {
        return _items;
    }



    public String cmd() {
        return _cmd;
    }

    public boolean isCashier() {
        return this.client.isCashier;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public boolean isWeiXin() {
        return isWeiXin;
    }

    public String userAgent() {
        return client.UserAgent;
    }

    public boolean isApp() {
        return this.client.isApp;
    }

    public URI uri() {
        return client.Uri;
    }

    public URI referrer() {
        return client.UrlReferrer;
    }

    public final static String KEY_HEADER_ARGUMENTS = "Arguments",
            KEY_ARGUMENTS_ITEMS = "KEY_ARGUMENTS_ITEMS";
}

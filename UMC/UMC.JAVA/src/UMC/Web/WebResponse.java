package UMC.Web;

import UMC.Data.JSON;


public class WebResponse {
    public int ClientEvent;
    WebMeta Headers = new WebMeta();

    public WebMeta headers() {
        return Headers;
    }

    protected void init(WebClient client) {

    }

    String _model, _cmd, _value;

    public void redirect(String mode, String cmd) {
        this.redirect(mode, cmd, true);
    }


    public void redirect(String mode, String cmd, String value, boolean endResponse) {

        this._model = mode;
        this._cmd = cmd;
        this._value = value;
        if (endResponse) {
            this.end();
        }
    }

    public void redirect(String mode, String cmd, String value) {
        this.redirect(mode, cmd, value, true);
    }

    public void redirect(String mode, String cmd, boolean endResponse) {

        this._model = mode;
        this._cmd = cmd;
        if (endResponse) {
            this.end();
        }
    }

    public void redirect(Object data) {
        this.Headers.put("Data", data);
        this.ClientEvent |= WebClient.OuterDataEvent;

        this.end();

    }

    public void redirect(String mode, String cmd, WebMeta arguments, boolean endResponse) {

        this._model = mode;
        this._cmd = cmd;
        this._value = JSON.serialize(arguments);
        if (endResponse) {
            this.end();
        }
    }

    void end() {
        throw new WebRuntime.AbortException();

    }

}

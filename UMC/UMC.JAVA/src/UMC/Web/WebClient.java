package UMC.Web;

import UMC.Data.Utility;
import UMC.Security.AuthManager;
import UMC.Security.Identity;
import UMC.Security.Membership;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.*;

public class WebClient {
    interface IWebRedirect {
        void redirect(URI uri);
    }

    class CommandKey {
        public String cmd;
        public String model;
        public String value;
    }

    final static int OuterDataEvent = 131072, Prompt = 2048;

    InputStream InputStream;
    public Boolean IsVerify;

    public void clear(int Event) {
        if ((this.ClientEvent & Event) == Event) {
            this.ClientEvent = this.ClientEvent ^ Event;
        }
        switch (Event) {
            case WebEvent.NORMAL:

                break;
        }
    }

    Map InnerHeaders;


    Map OuterHeaders;

    int ClientEvent;

    int RedirectTimes = 0;


    URI Uri;
    String UserHostAddress;
    URI UrlReferrer;


    public String UserAgent;

    public boolean isApp;
    private WebSession session;

    public WebSession session() {
        return session;
    }

    public boolean isCashier;

    public WebClient(WebSession session, URI uri, URI referrer, String UserAgent, String ip) {

        this.Uri = uri;
        this.UserHostAddress = ip;
        this.UserAgent = UserAgent;
        this.UrlReferrer = referrer;
        this.session = session;

        this.isCashier = Identity.current().isInRole(Membership.UserRole);
        if (Utility.isEmpty(UserAgent) == false)
            this.isApp = UserAgent.indexOf("WebADNuke POS Client") > -1;
        String header = session.header();
        if (UMC.Data.Utility.isEmpty(header) == false) {
            this.InnerHeaders = (Map) UMC.Data.JSON.deserialize(header);
            if (this.InnerHeaders == null) {
                this.InnerHeaders = new HashMap();
            }
        } else {

            this.InnerHeaders = new HashMap();

        }

    }


    void JSONP(String json, String p, Writer writer) throws IOException {
        Object[] cmd2s = (Object[]) UMC.Data.JSON.deserialize(json);

        CommandKey[] cmds = new CommandKey[cmd2s.length];
        int cindex = 0;

        for (Object k : cmd2s) {
            if (k instanceof Map) {
                Map map = (Map) k;
                CommandKey commandKey = new CommandKey();
                commandKey.cmd = (String) map.get("cmd");
                commandKey.model = (String) map.get("model");
                commandKey.value = (String) map.get("value");
                cmds[cindex] = commandKey;
                cindex++;
            }
        }


        if (UMC.Data.Utility.isEmpty(p) == false) {
            writer.write(p);
            writer.write('(');
        }
        if (cmds != null) {
            writer.write('{');
            boolean h = true;
            for (int index = 0; index < cmds.length; index++)//(var c in cmds)
            {
                CommandKey c = cmds[index];
                if (this.OuterHeaders != null) {
                    this.OuterHeaders.clear();
                }
                this.ClientEvent = 0;

                if (UMC.Data.Utility.isEmpty(c.value)) {
                    this.Command(c.model, c.cmd, "");
                } else if (c.value.indexOf("=") > -1) {
                    Map<String, String> QueryString = Utility.queryString(c.value);
                    this.Command(c.model, c.cmd, QueryString);
                } else {
                    this.Command(c.model, c.cmd, c.value);
                }

                if (h) {
                    h = false;
                } else {
                    writer.write(",");

                }
                if (UMC.Data.Utility.isEmpty(c.value)) {
                    UMC.Data.JSON.serialize(c.model + "." + c.cmd, writer);
                } else {
                    UMC.Data.JSON.serialize(c.model + "." + c.cmd + "." + c.value, writer);
                }
                writer.write(":");
                this.WriteTo(writer, uri -> {
                }, true);

            }
            writer.write("}");
        }
        if (UMC.Data.Utility.isEmpty(p) == false) {
            writer.write(")");
        }
    }

    public void Start(String start) {
        this.ClientEvent = WebEvent.NORMAL;

        switch (start) {
            case "true":

                this.Send(null);


                break;
            default:
                this.Command("Ticket", "Config", start);
                break;
        }
    }


    void Redirect(String model, String cmd, WebMeta meta) {
        if (Verify(model, cmd) == false) {
            return;
        }
        this.InnerHeaders.clear();
        if (meta != null) {
            this.InnerHeaders.put(WebRequest.KEY_HEADER_ARGUMENTS, meta.map());
        }
        this.ModelCommand(model, cmd, this.InnerHeaders);
        this.Send();
    }


    public void Command(String model, String cmd, String value) {

        if (Verify(model, cmd) == false) {
            return;
        }
        Redirect(model, cmd, value);


    }

    void Redirect(String model, String cmd, String value) {
        this.InnerHeaders.clear();
        if (!UMC.Data.Utility.isEmpty(value)) {

            InnerHeaders.put(cmd, value);
        }
        this.ModelCommand(model, cmd, InnerHeaders);
        this.Send();

    }

    void ModelCommand(String model, String cmd, Map header) {
        if (Utility.isEmpty(model) == false && Utility.isEmpty(cmd) == false) {
            header.put("POS-MODEL", model);
            header.put("POS-COMMAND", cmd);
        }
    }


    boolean Verify(String model, String cmd) {
        if (this.IsVerify == null) {

            if (this.session.authorization(model, cmd)) {
                this.IsVerify = true;
                return true;
            }
            Identity user = Identity.current();
            String key = String.format("%s.%s", model, cmd);
            WebAuthType authorizationType = WebAuthType.check;
            if (WebRuntime.authKeys.containsKey(key)) {
                authorizationType = WebRuntime.authKeys.get(key);
            } else if (WebRuntime.authKeys.containsKey(model)) {
                authorizationType = WebRuntime.authKeys.get(model);
            }
            if (authorizationType == WebAuthType.all) {
                this.IsVerify = true;
                return true;
            } else if (authorizationType == WebAuthType.user) {
                if (user.isInRole(Membership.UserRole)) {
                    this.IsVerify = true;
                    return true;
                }

            } else if (authorizationType == WebAuthType.userCheck) {
                if (user.isInRole(Membership.AdminRole)) {
                    this.IsVerify = true;
                    return true;
                } else if (user.isInRole(Membership.UserRole)) {
                    if (AuthManager.authorization(key)) {
                        this.IsVerify = true;
                        return true;

                    }
                }

            } else if (authorizationType == WebAuthType.check) {
                if (user.isInRole(Membership.AdminRole)) {
                    this.IsVerify = true;
                    return true;
                } else if (user.isAuthenticated()) {
                    if (AuthManager.authorization(key)) {
                        this.IsVerify = true;
                        return true;
                    }
                }
            } else if (authorizationType == WebAuthType.admin) {
                if (user.isInRole(Membership.AdminRole)) {
                    this.IsVerify = true;
                    return true;
                }

            } else if (authorizationType == WebAuthType.guest) {
                if (user.isAuthenticated()) {

                    this.IsVerify = true;
                    return true;
                } else {

                    this.OuterHeaders = new Hashtable();
                    this.ClientEvent = WebEvent.PROMPT | WebEvent.DATAEVENT;

                    this.OuterHeaders.put("Prompt", new WebMeta().put("Title", "提示", "Text", "您没有登录，请登录"));

                    this.OuterHeaders.put("DataEvent", new WebMeta().put("type", "Login"));

                    return false;
                }
            } else {

                this.IsVerify = true;
                return true;
            }


            this.OuterHeaders = new Hashtable();
            this.ClientEvent = WebEvent.PROMPT | WebEvent.DATAEVENT;

            this.OuterHeaders.put("Prompt", new WebMeta().put("Title", "提示", "Text", "您没有登录或权限受限"));

            this.OuterHeaders.put("DataEvent", new WebMeta().put("type", "Close"));
            ;
            return false;
        }
        return UMC.Data.Utility.isNull(this.IsVerify, false);

    }

    public void Command(String model, String cmd, Map<String, String> QueryString) {
        if (Verify(model, cmd) == false) {
            return;
        }
        Redirect(model, cmd, QueryString);


    }

    void Redirect(String model, String cmd, Map<String, String> QueryString) {

        switch (QueryString.size()) {
            case 0:
                this.Redirect(model, cmd, "");
                break;
            case 1:
                Map.Entry<String, String> entry = QueryString.entrySet().stream().findFirst().get();
                if (UMC.Data.Utility.isEmpty(entry.getValue())) {
                    this.Redirect(model, cmd, entry.getKey());
                    return;
                }
            default:
                String sinleValue = "";
                Map<String, String> hash = new Hashtable();

                for (String key : QueryString.keySet()) {
                    String value = QueryString.get(key);
                    if (UMC.Data.Utility.isEmpty(value)) {
                        sinleValue = key;

                    } else {
                        hash.put(key, value);
                    }
                }
                this.InnerHeaders.clear();
                Map header = this.InnerHeaders;
                header.put(model, hash);
                if (hash.size() == 0)
                    if (UMC.Data.Utility.isEmpty(sinleValue) == false) {
                        header.put(cmd, sinleValue);
                    }

                this.ModelCommand(model, cmd, header);
                this.Send();
                break;
        }

    }

    void SendDialog(String value) {
        Map<String, String> header = this.InnerHeaders;
        header.put(UIDialog.Dialog, value);
        this.Send();


    }


    void Send() {
        if (Utility.isNull(this.IsVerify, false) == false) {
            this.IsVerify = true;
        }
        Send(this.InnerHeaders);
    }


    void OutputHeader(WebMeta header) {
        if (this.OuterHeaders == null) {
            this.OuterHeaders = new Hashtable();
            ;
        }

        Map<String, Object> dic = header.map();

        for (String key : dic.keySet()) {
            Object value = dic.get(key);
            if (key.equalsIgnoreCase("DataEvent")) {
                if (this.OuterHeaders.containsKey(key)) {
                    ArrayList ats = new ArrayList();
                    Object ts = this.OuterHeaders.get(key);

                    if (ts.getClass().isArray()) {
                        for (int i = 0, l = Array.getLength(ts); i < l; i++) {
                            ats.add(Array.get(ts, i));
                        }
                    } else {

                        ats.add(ts);
                    }
                    if (value.getClass().isArray()) {
                        for (int i = 0, l = Array.getLength(ts); i < l; i++) {
                            ats.add(Array.get(ts, i));
                        }
                    } else {

                        ats.add(ts);

                    }
                    this.OuterHeaders.put(key, ats.toArray());
                } else {
                    this.OuterHeaders.put(key, value);
                }
            } else {
                this.OuterHeaders.put(key, value);
            }
        }

    }

    void SendDialog(Map<String, String> QueryString) {
        switch (QueryString.size()) {
            case 0:
                this.Start("true");
                break;
            case 1:
                Map.Entry<String, String> entry = QueryString.entrySet().stream().findFirst().get();
                if (UMC.Data.Utility.isEmpty(entry.getValue())) {
                    this.SendDialog(entry.getKey());
                    return;
                }
            default:
                Map header = this.InnerHeaders;

                Map Dialog = new HashMap();
                for (String key : QueryString.keySet()) {
                    if (UMC.Data.Utility.isEmpty(key)) {
                        continue;
                    }
                    Dialog.put(key, QueryString.get(key));

                }
                header.put(UIDialog.Dialog, Dialog);

                Send();
                break;
        }


    }


    private WebContext context;

    void Send(Map doc) {
        context = WebRuntime.ProcessRequest(doc, this);
        session.check(context);
        WebResponse response = context.response();
        WebRequest request = context.request();
        this.RedirectTimes++;
        int clientEvent = response.ClientEvent;
        this.InnerHeaders.clear();
        this.ModelCommand(request.model(), request.cmd(), this.InnerHeaders);

        if ((clientEvent & OuterDataEvent) == OuterDataEvent) {

            this.ClientEvent = clientEvent;
            this.OutputHeader(response.Headers);
            return;
        }
        if ((clientEvent & WebEvent.ASYNCDIALOG) == WebEvent.ASYNCDIALOG) {
            this.InnerHeaders.put(WebRequest.KEY_HEADER_ARGUMENTS, Utility.isNull(response.Headers.meta(WebRequest.KEY_HEADER_ARGUMENTS), new WebMeta()));
            if (UMC.Data.Utility.isEmpty(response._model) == false) {

                if ((clientEvent & WebClient.Prompt) != WebClient.Prompt) {
                    this.ModelCommand(response._model, response._cmd, this.InnerHeaders);
                    response._model = response._cmd = null;
                }
            }
        }
        if (UMC.Data.Utility.isEmpty(response._model) == false) {

            if (this.RedirectTimes > 10) {
                throw new IllegalArgumentException("请求重定向超过最大次数");
            }
            WebMeta args = response.Headers.meta(WebRequest.KEY_HEADER_ARGUMENTS);

            if (clientEvent != 0) {

                this.ClientEvent |= clientEvent;
                response.Headers.remove(WebRequest.KEY_HEADER_ARGUMENTS);
                OutputHeader(response.Headers);
            }

            if (UMC.Data.Utility.isEmpty(response._value)) {
                this.Redirect(response._model, response._cmd, args);
            } else {
                if (response._value.contains("&")) {
                    Map<String, String> nquery = Utility.queryString(response._value);//.. System.Web.HttpUtility.ParseQueryString(clientRedirect.Value);
                    this.Redirect(response._model, request._cmd, nquery);
                } else if (response._value.startsWith("{")) {
                    Map<String, Object> p = (Map<String, Object>) UMC.Data.JSON.deserialize(response._value);// as Hashtable;
                    ;
                    Map<String, String> pos = new HashMap<>();
                    for (Map.Entry<String, Object> key : p.entrySet()) {
                        pos.put(key.getKey(), String.valueOf(key.getValue()));
                    }

                    this.Redirect(response._model, response._cmd, pos);
                } else {
                    this.Redirect(response._model, response._cmd, response._value);
                }
            }
            return;
        }
        response.Headers.remove(WebRequest.KEY_HEADER_ARGUMENTS);
        OutputHeader(response.Headers);
        this.ClientEvent = this.ClientEvent | clientEvent;

    }

    public void WriteTo(Writer writer, IWebRedirect redirect) throws IOException {
        WriteTo(writer, redirect, false);
    }

    public void WriteTo(Writer writer, IWebRedirect redirect, boolean ismore) throws IOException {

        if (((this.ClientEvent) & OuterDataEvent) == OuterDataEvent) {
            Object data = this.OuterHeaders.get("Data");
            if (data instanceof WebClient) {
                WebClient d = (WebClient) data;

                d.WriteTo(writer, redirect);
                return;
            } else if (data instanceof URI) {
                redirect.redirect((URI) data);
                return;
            } else if (data instanceof WebFactory.XHR) {
                if (ismore == false)
                    this.session.storage(this.InnerHeaders, context);
                UMC.Data.JSON.serialize(data, writer);


                return;
            } else {
                UMC.Data.JSON.serialize(data, writer);
                return;
            }
        }
        Map<String, Object> map = null;
        if ((this.ClientEvent & WebEvent.ASYNCDIALOG) == WebEvent.ASYNCDIALOG) {
            this.session.storage(this.InnerHeaders, context);
        }
        if (context != null)
            map = session.outer(this, context);


        writer.write("{\"ClientEvent\":");

        writer.write(this.ClientEvent + "");
        if (this.OuterHeaders != null && this.OuterHeaders.size() > 0) {

            writer.write(",\"Headers\":");
            UMC.Data.JSON.serialize(this.OuterHeaders, writer);
        }
        if (map != null) {
            Set<String> set = map.keySet();
            for (String key : set) {
                writer.write(',');
                UMC.Data.JSON.serialize(key, writer);
                writer.write(':');
                UMC.Data.JSON.serialize(map.get(key), writer);
            }

        }
        writer.write("}");

    }

}
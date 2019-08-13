package UMC.Web;

import UMC.Data.JSON;
import UMC.Data.Utility;
import UMC.Security.AccessToken;
import UMC.Security.Membership;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class WebServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        WebRuntime.register(ClazzUtils.getClazzName(true));
        Package[] packages = Package.getPackages();
        for (Package p : packages) {
            if (p.isAnnotationPresent(Mapping.class)) {

                WebRuntime.register(ClazzUtils.getClazzName(p.getName(), true));
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ProcessRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ProcessRequest(req, resp);
    }

    protected void authorization(HttpServletRequest req, HttpServletResponse resp) {


        String[] Segments = req.getRequestURI().split("/");
        String CookieKey = "";

        if (Segments.length > 2) {
            CookieKey = Segments[2];
        }
        if (Utility.isEmpty(CookieKey)) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equalsIgnoreCase(Membership.SessionCookieName)) {
                        CookieKey = cookie.getValue();
                    }
                }
            }
            if (Utility.isEmpty(CookieKey)) {
                CookieKey = Utility.uuid(UUID.randomUUID());
                Cookie cookie = new Cookie(Membership.SessionCookieName, CookieKey);
                resp.addCookie(cookie);
            }
        }

        UUID sessionKey = Utility.uuid(CookieKey, true);

        String UserAgent = req.getHeader("User-Agent");
        String contentType = "Client/" + req.getRemoteAddr();
        if (UserAgent.indexOf("WebADNuke POS Client") > -1) {
            contentType = "App/" + req.getRemoteAddr();
        }

        Membership.Instance().Authorization(sessionKey, contentType);

        String referer = req.getHeader("referer");
        if (Utility.isEmpty(referer) == false) {
            URI uri = URI.create(referer);
            Map<String, String> query = Utility.queryString(uri.getQuery());

            UUID sp = Utility.uuid(query.get("sp"));
            if (sp != null) {
                if (sp.toString().equalsIgnoreCase(AccessToken.get("Spread-Id")) == false) {
                    AccessToken.set("Spread-Id", sp.toString());
                }
            }
        }


    }

    void ProcessRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf8");
        resp.setContentType("text/json;charset=utf-8");

        String ip = req.getRemoteAddr();
        URI Url = URI.create(req.getRequestURL().toString());

        String xRIP = req.getHeader("X-Real-IP");
        if (Utility.isEmpty(xRIP) == false) {
            ip = xRIP;
        }
        String chost = req.getHeader("CA-Host");

        if (Utility.isEmpty(chost) == false) {
            Url = URI.create(String.format("https://%s%s", chost, Url.getPath()));
        }

        authorization(req, resp);
        URI urireferer = null;
        String referer = req.getHeader("referer");
        if (Utility.isEmpty(referer) == false) {
            urireferer = URI.create(referer);
        }
        String UserAgent = req.getHeader("User-Agent");
        Process(req.getParameterMap(), req.getInputStream(), resp.getWriter(),
                Url, urireferer, ip, UserAgent, e -> {
                    try {
                        resp.sendRedirect(e.toString());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
    }

    public static List<WebMeta> auths() {
        List<WebMeta> metas = new LinkedList<>();
        if (WebRuntime.flows.size() > 0) {
            for (Map.Entry<String, List<Class>> entry : WebRuntime.flows.entrySet()) {

                Mapping mapping = (Mapping) entry.getValue().get(0).getAnnotation(Mapping.class);

                WebAuthType authType = WebRuntime.authKeys.get(entry.getKey());
                if (authType == WebAuthType.check) {
                    metas.add(new WebMeta().put("key", entry.getKey() + ".*").put("desc", mapping.desc()));

                } else if (authType == WebAuthType.userCheck) {
                    metas.add(new WebMeta().put("key", entry.getKey() + ".*").put("desc", mapping.desc()));

                }


            }
        }

        if (WebRuntime.activities.size() > 0) {
            for (Map.Entry<String, Map<String, Class>> entry : WebRuntime.activities.entrySet()) {
                Map<String, Class> map = entry.getValue();
                for (Map.Entry<String, Class> entry1 : map.entrySet()) {
                    Class t = entry1.getValue();


                    Mapping mapping = (Mapping) t.getAnnotation(Mapping.class);
                    WebAuthType authType = mapping.auth();

                    if (authType == WebAuthType.check) {
                        metas.add(new WebMeta().put("key", mapping.model() + "." + mapping.cmd()).put("desc", mapping.desc()));

                    } else if (authType == WebAuthType.userCheck) {
                        metas.add(new WebMeta().put("key", mapping.model() + "." + mapping.cmd()).put("desc", mapping.desc()));

                    }

                }


            }
        }
        return metas;

    }

    List<WebMeta> mapping() {
        List<WebMeta> metas = new LinkedList<>();
        if (WebRuntime.facClas.size() > 0) {
            for (Class t : WebRuntime.facClas) {
                WebMeta meta = new WebMeta();
                meta.put("type", t.getName());

                meta.put("name", "." + t.getSimpleName());
                metas.add(meta);

                Mapping mapping = (Mapping) t.getAnnotation(Mapping.class);
                if (Utility.isEmpty(mapping.desc()) == false) {
                    meta.put("desc", mapping.desc());

                }

            }

        }
        if (WebRuntime.flows.size() > 0) {
            for (Map.Entry<String, List<Class>> entry : WebRuntime.flows.entrySet()) {

                for (Class t : entry.getValue()) {
                    WebMeta meta = new WebMeta();
                    meta.put("type", t.getName());
                    metas.add(meta);
                    meta.put("auth", WebRuntime.authKeys.get(entry.getKey()).name());
                    meta.put("model", entry.getKey());

                    meta.put("name", entry.getKey() + ".");
                    Mapping mapping = (Mapping) t.getAnnotation(Mapping.class);
                    if (Utility.isEmpty(mapping.desc()) == false) {

                        meta.put("desc", mapping.desc());
                    }

                }

            }
        }

        if (WebRuntime.activities.size() > 0) {
            for (Map.Entry<String, Map<String, Class>> entry : WebRuntime.activities.entrySet()) {
                Map<String, Class> map = entry.getValue();
                for (Map.Entry<String, Class> entry1 : map.entrySet()) {
                    Class t = entry1.getValue();

                    WebMeta meta = new WebMeta();
                    metas.add(meta);

                    Mapping mapping = (Mapping) t.getAnnotation(Mapping.class);
                    meta.put("auth", mapping.auth().name());
                    meta.put("type", t.getName());
                    meta.put("name", mapping.model() + "." + mapping.cmd());
                    meta.put("cmd", entry1.getKey());
                    meta.put("model", entry.getKey());
                    if (Utility.isEmpty(mapping.desc()) == false) {

                        meta.put("desc", mapping.desc());

                    }
                }


            }
        }
        return metas;

    }

    void Process(Map<String, String[]> nvs, InputStream input, PrintWriter writer,
                 URI Url, URI UrlReferrer, String UserHostAddress, String UserAgent, WebClient.IWebRedirect redirec) {
        Map QueryString = new HashMap();

        String model = null, cmd = null;
        String start = null;// nvs.get("_start");
        String jsonp = null;//QueryString.get("jsonp");
        Set<Map.Entry<String, String[]>> set = nvs.entrySet();
        for (Map.Entry<String, String[]> entry : set) {
            String key = entry.getKey();
            switch (key) {
                case "_start":
                    start = String.join(",", entry.getValue());
                    break;
                case "_model":
                    model = String.join(",", entry.getValue());
                    break;
                case "_cmd":
                    cmd = String.join(",", entry.getValue());
                    break;
                case "jsonp":
                    jsonp = String.join(",", entry.getValue());
                    break;
                default:

                    if (!key.startsWith("_")) {
                        String value = String.join(",", entry.getValue());
                        try {

                            String urlencode = Utility.isNull(this.getServletConfig().getInitParameter("urlencode"), "ISO-8859-1");
                            if (urlencode.equalsIgnoreCase("utf-8") == false) {
                                value = new String(value.getBytes(urlencode), "utf-8");
                                key = new String(key.getBytes(urlencode), "utf-8");
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        QueryString.put(key, value);

                    }
                    break;
            }
        }
        String[] Segments = Url.getPath().split("/");
        if (Segments.length < 2) {


            JSON.serialize(mapping(), writer);
            return;
        }
        if (Utility.isEmpty(model)) {
            if (Segments.length > 4 && Segments[Segments.length - 1].indexOf('.') == -1) {
                if (Segments.length > 4) {
                    model = Segments[3];
                    cmd = Segments[4];
                }
                if (Segments.length > 5) {
                    QueryString.put(Segments[5], null);
                }

            }
        }
        if (Utility.isEmpty(model) == false) {
            switch (model) {
                case "System":
                    if (Utility.isEmpty(jsonp) == false) {
                        writer.write(jsonp);
                        writer.write('(');
                    }
                    switch (cmd) {
                        case "TimeSpan":
                            writer.write((System.currentTimeMillis() / 1000) + "");
                            break;
                        case "Mapping":
                            JSON.serialize(mapping(), writer);
                            break;
                        case "Debug":
                            if (Utility.isEmpty(UMC.Security.AccessToken.get("Debug"))) {
                                UMC.Security.AccessToken.set("Debug", "OK");
                                writer.write("{\"Text\":\"当前账户开启了调试模式\"}");
                            } else {

                                UMC.Security.AccessToken.set("Debug", null);
                                writer.write("{\"Text\":\"当前账户关闭了调试模式\"}");
                            }
                            break;
                    }
                    if (Utility.isEmpty(jsonp) == false) {
                        writer.write(")");
                    }
                    return;
                case "Upload":
                    switch (cmd) {
                        case "Command":

                            StringBuffer stringBuilder = new StringBuffer();
                            try {
                                URL url = new URL(QueryString.get("src").toString());
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                InputStream inputStream = connection.getInputStream();
                                InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");


                                char[] cha = new char[1024];
                                int len = 0;
                                while ((len = reader.read(cha)) != -1) {
                                    stringBuilder.append(cha, 0, len);
                                }
                                reader.close();
                                inputStream.close();


                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Object omap = JSON.deserialize(stringBuilder.toString());
                            if (omap instanceof Map) {
                                Map map = (Map) omap;
                                model = (String) map.get("_model");
                                cmd = (String) map.get("_cmd");
                                map.remove("_model");
                                map.remove("_cmd");
                                QueryString.clear();
                                QueryString.putAll(map);
                            } else {
                                return;
                            }
                            break;
                    }
                    break;

            }
        }


        WebClient client = new WebClient(WebSession.Instance(), Url, UrlReferrer, UserAgent, UserHostAddress);
        client.InputStream = input;

        if (Utility.isEmpty(jsonp) == false && jsonp.startsWith("app")) {
            client.isApp = true;
        }

        if (Utility.isEmpty(start) == false) {
            client.Start(start);
        } else if (Utility.isEmpty(model)) {

            client.SendDialog(QueryString);
        } else {
            if (Utility.isEmpty(cmd)) {
                if (model.startsWith("[") == false) {
                    throw new IllegalArgumentException("command is empty");

                }
            } else {
                client.Command(model, cmd, QueryString);
            }
        }


        if (Utility.isEmpty(model) == false && model.startsWith("[") && Utility.isEmpty(cmd)) {
            try {
                client.JSONP(model, jsonp, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (Utility.isEmpty(jsonp) == false) {
                writer.write(jsonp);
                writer.write('(');
            }
            try {
                client.WriteTo(writer, redirec);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Utility.isEmpty(jsonp) == false) {
                writer.write(")");
            }
        }


    }
}

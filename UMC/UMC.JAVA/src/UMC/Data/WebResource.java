package UMC.Data;


import UMC.Security.Principal;
import UMC.Web.WebMeta;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.security.Security;
import java.util.UUID;
/**
 * Web资源管理器，可以在App_Data/WebADNuke/assembly.xml中注册
 */
public  class WebResource extends DataProvider {


    public static final String ImageResource = "~/images/";

    public static final String UserResources = "~/UserResources/";
    static WebResource _Instance;

    public static WebResource Instance() {
        if (_Instance == null) {
            _Instance = (WebResource) Utility.createObject("WebResource");
            if (_Instance == null) {
                _Instance = new WebResource();
                _Instance.provider = Provider.create("WebResource", "UMC.Data.WebResource");

            }
        }
        return _Instance;
    }

    public String WebDomain() {
        return Utility.isNull(this.provider.get("domain"), "/");
    }


    public String ResolveUrl(String path) {
        String vUrl = path;
        if (path.startsWith("~/")) {
            vUrl = path.substring(1);
        } else if (path.startsWith("~")) {
            vUrl = "/" + path.substring(1);
        }
        String src = this.provider.get("src");
        if (Utility.isEmpty(src)) {

            String vpath = this.provider.get("authkey");

            if (Utility.isEmpty(vpath) == false) {
                String code = Utility.parseEncode(Utility.authCode(Utility.uuid(vpath)), 36);
                vpath = code;// + "/";
            }

            return String.format("http://image.365lu.cn/%s%s", vpath, vUrl);


        }
        return src + vUrl;
    }

    public String ResolveUrl(UUID id, Object seq, Object size) {
        String kdey = "";
        switch (size.toString()) {
            case "0":
                break;
            case "1":
                kdey = "!350";
                break;
            case "2":
                kdey = "!200";
                break;
            case "3":
                kdey = "!150";
                break;
            case "4":
                kdey = "!100";
                break;
            case "5":
                kdey = "!50";
                break;
        }
        return ResolveUrl(String.format("%s%s/%s/0.jpg%s", ImageResource, id, seq, kdey));

    }

    public String ImageResolve(UUID id, String key, int size) {
        int seq = UMC.Data.Utility.parse(key, -1);
        if (seq < 0) {
            seq = key.hashCode();
            if (seq > 0) {
                seq = -seq;
            }
        }
        String kdey = "";
        switch (size) {
            case 0:
                break;
            case 1:
                kdey = "!350";
                break;
            case 2:
                kdey = "!200";
                break;
            case 3:
                kdey = "!150";
                break;
            case 4:
                kdey = "!100";
                break;
            case 5:
                kdey = "!50";
                break;
        }
        return ResolveUrl(String.format("%s%s/%s/0.jpg%s", ImageResource, id, seq, kdey));

    }

    public void Transfer(URI src, UUID guid, int seq, String type) {

        String vpath = this.provider.get("authkey");
        if (Utility.isEmpty(vpath) == false) {
            String code = Utility.parseEncode(Utility.authCode(Utility.uuid(vpath)), 36);
            vpath = code + "/";


            String key = String.format("%simages/%s/%s/%s.%s", vpath, guid, seq, 0, type.toLowerCase());


            String sts = String.format("https://ali.365lu.cn/OSS/Transfer/%s", this.provider.get("authkey"));
            if (Utility.isEmpty(sts) == false) {
                try {

                    HttpURLConnection connection = (HttpURLConnection) URI.create(sts).toURL().openConnection();


                    connection.setRequestMethod("POST");


                    connection.setRequestProperty("Content-Type", "binary/octet-stream");
                    connection.setDoOutput(true);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(JSON.serialize(new WebMeta().put("src", src.toString(), "key", key)).getBytes());
                    outputStream.flush();
                    ;
                    outputStream.close();

                    int rescode = connection.getResponseCode();

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void Transfer(URI src, UUID guid, int seq) {
        Transfer(src, guid, seq, "jpg");
    }

}
package UMC.Web;


import UMC.Data.DataProvider;
import UMC.Data.IJSON;
import UMC.Data.JSON;
import UMC.Data.Utility;
import UMC.Security.AccessToken;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Set;

public abstract class WebFactory implements IWebFactory {
    static class XHR implements IJSON {
        public XHR(String xhr) {
            this.expression = xhr;
        }

        public String expression;


        @Override
        public void write(Writer writer) {

            try {
                writer.write(expression);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void read(String key, Object value) {

        }
    }

    class XHRFlow extends WebFlow {

        @Override
        public WebActivity firstActivity() {
            StringBuilder sb = new StringBuilder();
            sb.append(uri.toString());
            WebRequest req = this.context().request();
            sb.append(req.uri().getPath().split("/")[1]);
            sb.append("/");
            sb.append(AccessToken.token().toString());
            sb.append("/");
            if (req.headers().containsKey(UIDialog.Dialog)) {
                WebMeta meta = req.headers().meta(UIDialog.Dialog);
                if (meta != null) {

                    Map<String, Object> map = meta.map();
                    Set<Map.Entry<String, Object>> set = map.entrySet();
                    boolean isOne = true;
                    for (Map.Entry<String, Object> entry : set) {
                        if (isOne) {
                            sb.append("?");
                            isOne = false;
                        } else {
                            sb.append("&");
                        }
                        try {
                            sb.append(URLEncoder.encode(entry.getKey(), "utf-8"));
                            sb.append("=");
                            sb.append(URLEncoder.encode(entry.getValue().toString(), "utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    String dg = req.headers().get(UIDialog.Dialog);
                    sb.append("?");
                    try {
                        sb.append(URLEncoder.encode(dg, "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                sb.append(req.model());
                sb.append("/");
                sb.append(req.cmd());
                sb.append("/");
                WebMeta meta = req.sendValues();
                if (meta != null) {

                    Map<String, Object> map = meta.map();
                    Set<Map.Entry<String, Object>> set = map.entrySet();
                    boolean isOne = true;
                    for (Map.Entry<String, Object> entry : set) {
                        if (isOne) {
                            sb.append("?");
                            isOne = false;
                        } else {
                            sb.append("&");
                        }
                        try {
                            sb.append(URLEncoder.encode(entry.getKey(), "utf-8"));
                            sb.append("=");
                            sb.append(URLEncoder.encode(map.get(entry.getValue()).toString(), "utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    String dg = req.sendValue();
                    if (Utility.isEmpty(dg) == false) {
                        sb.append("?");
                        try {
                            sb.append(URLEncoder.encode(dg, "utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(sb.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.addRequestProperty("User-Agent", req.userAgent());

                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");

                char[] cha = new char[1024];
                int len = 0;
                while ((len = reader.read(cha)) != -1) {
                    stringBuilder.append(cha, 0, len);
                }
                reader.close();


                inputStream.close();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String xhr = stringBuilder.toString();
            String eventPfx = "{\"ClientEvent\":";
            if (xhr.startsWith(eventPfx)) {

                int index = stringBuilder.indexOf(",");
                if (index > -1) {
                    if ((Utility.parse(xhr.substring(eventPfx.length(), index), 0) & WebEvent.ASYNCDIALOG) == WebEvent.ASYNCDIALOG) {

                        this.context().response().redirect(new XHR(stringBuilder.toString()));
                    } else {

                        this.context().response().redirect(JSON.expression(stringBuilder.toString()));
                    }
                } else {

                    this.context().response().redirect(JSON.expression(stringBuilder.toString()));
                }
            } else {

                this.context().response().redirect(JSON.expression(stringBuilder.toString()));
            }

            return WebActivity.Empty;
        }
    }

    private String[] models;
    private URI uri;

    protected void registerModel(URI uri, String... models) {
        this.models = models;
        this.uri = uri;
    }

    /**
     * 请在此方法中完成url与model的注册,即调用registerModel方法
     *
     * @param context
     */
    public abstract void init(WebContext context);

    @Override
    public WebFlow flowHandler(String mode) {
        if (this.models != null && this.uri != null) {
            for (String m : this.models) {
                if (m.equals(mode)) {

                    return new XHRFlow();
                }
            }
        }
        return WebFlow.Empty;
    }
}

package UMC.Web;

import java.lang.reflect.Array;
import java.util.*;

public class WebContext {
    WebRuntime runtime;



//        this.runtime =runtime;

    public WebRequest request() {
        return runtime.request;
    }

    public WebResponse response() {
        return runtime.response;
    }

    protected void complete() {

    }

    public WebActivity activity() {
        return runtime.cuurActivity;
    }

    public WebFlow flow() {
        return runtime.cuurFlow;
    }


    public Map items() {
        return runtime.items;
    }

    protected void init(WebClient client) {
    }

    public static void end() {

        throw new WebRuntime.AbortException();
    }

    public void reset() {
        runtime.response.ClientEvent |= WebEvent.RESET;
    }

    public void send(WebMeta data, boolean endResponse) {
        WebResponse response = this.response();
        response.ClientEvent |= WebEvent.DATAEVENT;
        if (response.Headers.containsKey("DataEvent")) {
            Object ts = response.Headers.map().get("DataEvent");
            if (ts instanceof WebMeta) {
                response.Headers.put("DataEvent", new WebMeta[]{(WebMeta) ts, data});

            } else if (ts instanceof Map) {

                response.Headers.put("DataEvent", new WebMeta[]{new WebMeta((Map) ts), data});

            } else if (ts.getClass().isArray()) {

                WebMeta[] mts = new WebMeta[Array.getLength(ts) + 1];
                Arrays.copyOf(mts, mts.length - 2);

                mts[mts.length - 1] = data;

                response.Headers.put("DataEvent", mts);
            } else {
                response.Headers.put("DataEvent", data);
            }

        } else {

            response.Headers.put("DataEvent", data);
        }
        if (endResponse) {
            response.ClientEvent ^= response.ClientEvent & WebEvent.NORMAL;
            this.end();
        }

    }

    public void send(String type, WebMeta data, boolean endResponse) {
        this.send(data.put("type", type), endResponse);
    }

    public void send(String type, boolean endResponse) {
        WebMeta data = new WebMeta();
        send(type, data, endResponse);
    }


}

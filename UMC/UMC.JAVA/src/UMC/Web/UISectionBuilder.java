package UMC.Web;


public class UISectionBuilder {
    WebMeta _data = new UMC.Web.WebMeta();

    public UISectionBuilder(String model, String cmd) {
        _data.put("model", model, "cmd", cmd);//.put("search", search);

    }

    public UISectionBuilder(String model, String cmd, WebMeta search) {
        _data.put("model", model, "cmd", cmd).put("search", search);
    }

    public UISectionBuilder refreshEvent(String... eventName) {

        _data.put("RefreshEvent", String.join(",", eventName));
        return this;
    }

    public UISectionBuilder dataEvent(String... eventName) {

        _data.put("DataEvent", String.join(",", eventName));
        return this;
    }

    public UISectionBuilder scanning(UIClick click) {

        _data.put("Scanning", click);
        return this;
    }

    public UISectionBuilder closeEvent(String... eventName) {

        _data.put("CloseEvent", String.join(",", eventName));
        return this;
    }

    public WebMeta builder() {
        return new WebMeta(_data.map()).put("type", "Pager");
    }

    public void builder(WebContext context, boolean end) {
        context.send(new WebMeta(_data.map()).put("type", "Pager"), end);
    }
}
package UMC.Web.UI;


import UMC.Web.WebMeta;
import UMC.Web.UICell;
import UMC.Web.UIClick;


public class UICMS extends UICell {
    @Override
    public Object data() {
        return data;
    }

    String Type;

    @Override
    public String type() {
        return Type;
    }

    WebMeta data;

    private UICMS(WebMeta data) {
        this.data = data;
    }

    public static UICMS createMax(UIClick click, WebMeta data) {
        UICMS t = new UICMS(data);
        t.data.put("click", click);
        t.Type = "CMSMax";
        return t;

    }

    public static UICMS createMax(UIClick click, WebMeta data, String src) {
        UICMS t = new UICMS(data);
        t.data.put("src", src);
        t.data.put("click", click);
        t.Type = "CMSMax";
        return t;
    }

    public static UICMS createOne(UIClick click, WebMeta data, String src) {
        UICMS t = new UICMS(data);
        t.data.put("src", src);
        t.data.put("click", click);
        t.Type = "CMSOne";
        return t;

    }

    public static UICMS createThree(UIClick click, WebMeta data, String... src) {

        UICMS t = new UICMS(data);
        t.data.put("images", src);
        t.data.put("click", click);
        t.Type = "CMSThree";
        return t;
    }

    public UICMS desc(String desc) {
        data.put("desc", desc);
        return this;
    }

    public UICMS title(String title) {
        this.data.put("title", title);
        return this;

    }

    public UICMS right(String right) {
        data.put("right", right);
        return this;

    }

    public UICMS left(String left) {
        data.put("left", left);
        return this;

    }
}
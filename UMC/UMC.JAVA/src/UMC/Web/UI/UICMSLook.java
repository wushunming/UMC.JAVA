package UMC.Web.UI;


import UMC.Web.WebMeta;
import UMC.Web.UICell;
import UMC.Web.UIClick;

public class UICMSLook extends UICell {
    @Override
    public Object data() {
        return data;
    }


    @Override
    public String type() {
        return "CMSLook";
    }

    public static UICMSLook create(String src, UIClick click, WebMeta data) {
        UICMSLook t = new UICMSLook(data);
        t.data.put("src", src);
        t.data.put("click", click);

        return t;

    }

    WebMeta data;

    private UICMSLook(WebMeta data) {
        this.data = data;
    }

    public UICMSLook title(String title) {
        this.format("title", title);
        return this;
    }

    public UICMSLook desc(String desc) {
        this.format("desc", desc);
        return this;
    }
}
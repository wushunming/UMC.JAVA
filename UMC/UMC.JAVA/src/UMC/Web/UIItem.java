package UMC.Web;


import java.io.Writer;

public class UIItem implements UMC.Data.IJSON {
    public static UIItem Create(WebMeta meta) {

        UIItem item = new UIItem("", "");
        item.meta = meta;
        return item;
    }

    public UIItem Gradient(int startColor, int endColor) {

        meta.put("startColor", UIStyle.intParseColor(startColor));
        meta.put("endColor", UIStyle.intParseColor(endColor));
        return this;
    }

    public UIItem Style(UIStyle style) {

        meta.put("style", style);
        return this;
    }

    public UIItem Click(UIClick click) {

        meta.put("click", click);
        return this;
    }

    private WebMeta meta = new WebMeta();

    public UIItem(String title, String desc) {
        meta.put("title", title);
        meta.put("desc", desc);

    }

    public UIItem Src(String url) {

        meta.put("src", url);
        return this;
    }


    @Override
    public void write(Writer writer) {
        UMC.Data.JSON.serialize(this.meta, writer);

    }

    @Override
    public void read(String key, Object value) {

    }
}
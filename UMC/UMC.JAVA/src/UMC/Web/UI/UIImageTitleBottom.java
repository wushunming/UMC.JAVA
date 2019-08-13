package UMC.Web.UI;

import UMC.Web.WebMeta;
import UMC.Web.UICell;
import UMC.Web.UIClick;
import UMC.Web.UIStyle;

public class UIImageTitleBottom extends UICell {
    @Override
    public Object data() {
        return data;
    }

    @Override
    public String type() {
        return "ImageTitleBottom";
    }

    public static UIImageTitleBottom create(String src) {
        UIImageTitleBottom t = new UIImageTitleBottom(new WebMeta());
        t.data.put("src", src);
        return t;

    }

    private UIImageTitleBottom(WebMeta data) {
        this.data = data;
    }

    public static UIImageTitleBottom create(WebMeta data, String src) {
        UIImageTitleBottom t = new UIImageTitleBottom(data);
        t.data.put("src", src);
        return t;

    }

    public static UIImageTitleBottom create(UIClick click, WebMeta data, String src) {
        UIImageTitleBottom t = new UIImageTitleBottom(data);
        t.data.put("src", src);
        t.data.put("click", click);
        return t;

    }

    public UIImageTitleBottom max(String src) {
        this.data.put("max", src);
        return this;
    }

    public UIImageTitleBottom left(String desc) {
        format("left", desc);
        return this;
    }

    public UIImageTitleBottom title(String desc) {
        this.format("title", desc);
        return this;
    }

    public UIImageTitleBottom right(String price) {
        this.format("right", price);
        return this;
    }

    public UIImageTitleBottom left(String price, String unit) {
        this.data.put("price", price, "unit", unit);
        this.format("left", "¥{1:price}/{1:unit}");
        this.style().name("price", new UIStyle().size(20).color(0xdb3652)).name("unit", new UIStyle().size(15).color(0x999)).name("orgin", new UIStyle().color(0x999).size(12).delLine());
        return this;
    }

    public UIImageTitleBottom left(String price, String orgin, String unit) {
        this.data.put("price", price, "unit", unit, "orgin", orgin);
        this.format("left", "¥{1:price}/{1:unit} 原价:{3:orgin}");
        this.style().name("price", new UIStyle().size(14).color(0xdb3652)).name("unit", new UIStyle().size(12).color(0x999)).name("orgin", new UIStyle().color(0x999).size(12).delLine());
        return this;
    }

    public UIImageTitleBottom click(UIClick click) {
        this.data.put("click", click);
        return this;
    }

    private WebMeta data;

}

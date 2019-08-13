package UMC.Web.UI;


import UMC.Web.WebMeta;
import UMC.Web.UICell;
import UMC.Web.UIClick;
import UMC.Web.UIStyle;

public class UIDiscount extends UICell {

    WebMeta data;

    @Override
    public Object data() {
        return data;
    }

    @Override
    public String type() {
        return "Discount";
    }

    private UIDiscount(WebMeta data) {
        this.data = data;
    }

    public static UIDiscount create() {

        UIDiscount t = new UIDiscount(new WebMeta());
        return t;
    }

    public static UIDiscount create(UIClick click) {
        UIDiscount t = create();
        t.data.put("click", click);
        return t;
    }

    public UIDiscount click(UIClick click) {

        data.put("click", click);
        return this;
    }

    public UIDiscount gradient(int startColor, int endColor) {

        data.put("endColor", UIStyle.intParseColor(endColor));

        data.put("startColor", UIStyle.intParseColor(startColor));
       
        return this;
    }

    public UIDiscount desc(String desc) {
        data.put("desc", desc);
        return this;
    }

    public UIDiscount title(String title) {
        this.format("title", title);
        return this;

    }

    public UIDiscount end(String end) {
        data.put("end", end);
        return this;

    }

    public UIDiscount start(String start) {
        data.put("start", start);
        return this;

    }

    public UIDiscount value(String value) {
        this.data.put("value", value);
        return this;

    }

    public UIDiscount state(String state) {
        this.data.put("state", state);
        return this;

    }
}
package UMC.Web.UI;

import UMC.Web.WebMeta;
import UMC.Web.UICell;
import UMC.Web.UIClick;

public class UIImageTextValue extends UICell {
    @Override
    public Object data() {
        return data;
    }

    @Override
    public String type() {
        return "ImageTextValue";
    }


    private UIImageTextValue(WebMeta data) {
        this.data = data;
    }

    public static UIImageTextValue create(String src, String text, String value) {
        UIImageTextValue t = new UIImageTextValue(new WebMeta());
        t.data.put("src", src);
        t.data.put("text", text).put("value", value);
        return t;

    }

    public UIImageTextValue text(String text) {
        data.put("text", text);
        return this;
    }

    public UIImageTextValue value(String value) {
        data.put("value", value);
        return this;
    }

    public UIImageTextValue put(String name, String value) {
        data.put(name, value);
        return this;

    }

    public UIImageTextValue click(UIClick click) {
        data.put("click", click);
        return this;

    }

    WebMeta data;

}

package UMC.Web.UI;


import UMC.Web.WebMeta;
import UMC.Web.UICell;
import UMC.Web.UIClick;

public class UIImageTitleDescBottom extends UICell {
    private UIImageTitleDescBottom(WebMeta data) {
        this.data = data;
    }

    public static UIImageTitleDescBottom Create(WebMeta data, String src) {
        UIImageTitleDescBottom t = new UIImageTitleDescBottom(data);
        t.data.put("src", src);
        return t;

    }

    @Override
    public Object data() {
        return data;
    }

    WebMeta data;

    @Override
    public String type() {
        return "ImageTitleDescBottom";
    }


    public UIImageTitleDescBottom desc(String desc) {
        this.format("desc", desc);
        return this;

    }

    public UIImageTitleDescBottom click(UIClick click) {
        this.data.put("click", click);
        return this;
    }

    public UIImageTitleDescBottom title(String desc) {

        this.format("title", desc);
        return this;

    }

    public UIImageTitleDescBottom left(String price) {
        this.format("left", price);
        return this;

    }

    public UIImageTitleDescBottom right(String price) {
        this.format("right", price);
        return this;

    }
}
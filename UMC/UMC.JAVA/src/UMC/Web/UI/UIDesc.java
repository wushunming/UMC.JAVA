package UMC.Web.UI;


import UMC.Web.WebMeta;
import UMC.Web.UICell;
import UMC.Web.UIClick;

public class UIDesc extends UICell {
    public UIDesc(String desc) {
        this.data = new WebMeta().put("desc", desc);
    }

    public UIDesc click(UIClick click) {

        this.data.put("click", click);
        return this;
    }

    public UIDesc(WebMeta desc) {
        this.data = desc;// new WebMeta().put("desc", desc);


    }

    public UIDesc desc(String desc) {
        this.format("desc", desc);
        return this;
    }

    WebMeta data;

    @Override
    public Object data() {
        return data;
    }

    @Override
    public String type() {
        return "Desc";
    }
}

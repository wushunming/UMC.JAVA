package UMC.Web.UI;

import UMC.Web.WebMeta;
import UMC.Web.UICell;
import UMC.Web.UIClick;
import UMC.Web.UIStyle;

public class UITitleMore extends UICell {
    @Override
    public Object data() {
        return data;
    }

    @Override
    public String type() {
        return "TitleMore";
    }


    WebMeta data;

    public UITitleMore(String title) {
        this.data = new WebMeta().put("title", title);
    }

    public UITitleMore title(String title) {
        this.format("title", title);
        return this;
    }

    public UITitleMore more(String more) {
        this.format("more", more);
        return this;
    }

    public UITitleMore click(UIClick click) {
        this.data.put("click", click);
        this.data.put("more", '\uE905');
        this.style().name("more", new UIStyle().font("wdk").size(12));
        return this;
    }
}

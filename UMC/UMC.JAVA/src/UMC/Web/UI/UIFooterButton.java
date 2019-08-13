package UMC.Web.UI;

import UMC.Web.*;

public class UIFooterButton extends UICell {
    @Override
    public Object data() {
        return data;
    }

    @Override
    public String type() {
        return "UIFooterButton";
    }


    WebMeta data;

    public static UIFooterButton create(WebMeta data) {
        UIFooterButton t = new UIFooterButton(data);
        return t;

    }

    public static UIFooterButton create() {
        UIFooterButton t = new UIFooterButton(new WebMeta());
        return t;

    }

    private UIFooterButton(WebMeta data) {
        this.data = data;
    }

    public UIFooterButton title(String title) {
        this.format("title", title);
        return this;
    }


    public void button(UIEventText... btns) {
        data.put("buttons", btns);
    }
}
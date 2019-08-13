package UMC.Web.UI;


import UMC.Web.WebMeta;
import UMC.Web.UIEventText;
import UMC.Web.UIStyle;
import UMC.Web.UICell;

import java.io.Writer;

public class UICommentCell extends UICell {

    @Override
    public Object data() {
        return data;
    }

    @Override
    public String type() {
        return "Comment";
    }

    public static class Image {
        public String max;
        public String src;
    }

    public static class Reply implements UMC.Data.IJSON {
        public String title;

        public String content;
        public UIStyle style;

        public WebMeta data;

        @Override
        public void write(Writer writer) {
            UMC.Data.JSON.serialize(new WebMeta().put("format", new WebMeta().put("content", this.content, "title", this.title)).put("value", this.data).put("style", this.style), writer);

        }

        @Override
        public void read(String key, Object value) {

        }

    }

    private WebMeta data;

    public UICommentCell(String src) {
        this.data = new WebMeta().put("src", src);
    }

    public UICommentCell name(String name, String value) {

        this.data.put(name, value);
        return this;
    }

    public UICommentCell ame(String name) {
        this.format("name", name);
        return this;
    }

    public UICommentCell time(String title) {
        this.format("time", title);
        return this;
    }

    public UICommentCell content(String content) {
        this.format("content", content);
        return this;
    }

    public UICommentCell images(Image... images) {
        this.data.put("image", images);
        return this;

    }

    public String Id;

    public UICommentCell replys(Reply... replys) {
        //foreach(var re in replys)
        this.data.put("replys", replys);
        return this;

    }

    public UICommentCell button(UIEventText... btns) {
        this.data.put("buttons", btns);
        return this;

    }
}
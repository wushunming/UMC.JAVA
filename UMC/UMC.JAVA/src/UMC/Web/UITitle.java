package UMC.Web;


import java.io.Writer;

public class UITitle implements UMC.Data.IJSON {
    public UITitle(String title) {
        meta.put("text", title);
    }

    public UITitle name(String name, String value) {
        meta.put(name, value);
        return this;
    }

    public UITitle() {

    }

    public String title()

    {

        return meta.get("text");
    }

    public UITitle title(String value)

    {

        meta.put("text", value);
        return this;

    }

    public UITitle Float() {
        meta.put("float", true);
        return this;
    }

    public UITitle left(char icon, UIClick click) {
        this.left(new UIEventText().icon(icon).click(click));
        return this;

    }

    public UITitle left(UIEventText text) {
        meta.put("left", text);
        return this;
    }

    public UITitle left(char icon, String click) {
        this.left(new UIEventText().icon(icon).click(click));
        return this;
    }

    public UITitle right(char icon, String click) {
        this.right(new UIEventText().icon(icon).click(click));
        return this;
    }

    public UITitle right(UIEventText text) {
        meta.put("right", text);
        return this;
    }

    public UITitle right(char icon, UIClick click) {
        this.right(new UIEventText().icon(icon).click(click));
        return this;
    }

    public static UITitle TabTitle() {
        UITitle t = new UITitle();
        t.meta.put("type", "Tab");
        return t;
    }

    WebMeta meta = new WebMeta();

    @Override
    public void write(Writer writer) {
        UMC.Data.JSON.serialize(this.meta, writer);

    }

    @Override
    public void read(String key, Object value) {

    }
}
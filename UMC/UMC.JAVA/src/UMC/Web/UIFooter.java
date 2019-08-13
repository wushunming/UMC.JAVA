package UMC.Web;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class UIFooter extends UIHeader {
    List<Object> _icons = new LinkedList<>();
    List<Object> _btons = new LinkedList<>();

    public UIFooter cart() {
        _icons.add("-");
        if (this.meta.containsKey("icons") == false) {
            this.meta.put("icons", _icons);
        }
        return this;
    }

    public UIFooter text(UIEventText... text) {
        _btons.addAll(Arrays.asList(text));
        if (this.meta.containsKey("buttons") == false) {
            this.meta.put("buttons", _btons);
        }
        return this;

    }

    public UIFooter icon(UIEventText... icons) {
        _icons.addAll(Arrays.asList(icons));
        if (this.meta.containsKey("icons") == false) {
            this.meta.put("icons", _icons);
        }
        return this;

    }

    public boolean fixed() {


        return this.meta.containsKey("fixed");
    }

    public UIFooter fixed(boolean value) {
        if (value) {
            this.meta.put("fixed", true);
        } else {
            this.meta.remove("fixed");

        }
        return this;
    }
}
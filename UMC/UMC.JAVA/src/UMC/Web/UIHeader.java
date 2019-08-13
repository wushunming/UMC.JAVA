package UMC.Web;


import UMC.Data.Utility;

import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class UIHeader implements UMC.Data.IJSON {


    public static class Portrait implements UMC.Data.IJSON {
        WebMeta meta = new WebMeta();

        public Portrait(String src) {
            meta.put("src", src);

        }

        public Portrait() {

        }

        public Portrait key(String Key) {
            meta.put("key", Key);
            return this;

        }

        public Portrait click(UIClick click) {

            meta.put("click", click);
            return this;
        }

        public Portrait title(String title) {
            meta.put("title", title);
            return this;
        }

        public Portrait time(String time) {
            meta.put("time", time);
            return this;
        }

        public Portrait value(String value) {
            meta.put("value", value);
            return this;
        }

        public Portrait desc(String desc) {
            meta.put("desc", desc);
            return this;
        }

        public Portrait gradient(int startColor, int endColor) {


            meta.put("startColor", UIStyle.intParseColor(startColor));

            meta.put("endColor", UIStyle.intParseColor(endColor));
            return this;
        }

        @Override
        public void write(Writer writer) {
            UMC.Data.JSON.serialize(this.meta, writer);

        }

        @Override
        public void read(String key, Object value) {

        }
    }

    public static class Profile implements UMC.Data.IJSON {
        WebMeta meta = new WebMeta();

        public Profile(String name, String number, String src) {
            meta.put("name", name);
            meta.put("number", number);
            meta.put("src", src);

        }

        public Profile(String name, String src) {
            meta.put("name", name);
            meta.put("src", src);

        }

        public Profile account(String amount, String tip, String tag) {
            return account(amount, tip, tag, null);
        }

        public Profile account(UIClick click) {
            return account(null, null, null, click);
        }

        public Profile account(String amount, String tip, String tag, UIClick click) {
            WebMeta acount = new WebMeta().put("amount", amount);
            if (Utility.isEmpty(tip) == false) {
                acount.put("tag", tip);
            }

            if (click != null) {
                acount.put("click", click);
            }
            meta.put("account", acount);
            return this;
        }

        public Profile click(UIClick click) {

            this.meta.put("click", click);
            return this;
        }

        @Override
        public void write(Writer writer) {
            UMC.Data.JSON.serialize(this.meta, writer);

        }

        @Override
        public void read(String key, Object value) {

        }

        public Profile gradient(int startColor, int endColor) {

            meta.put("startColor", UIStyle.intParseColor(startColor));

            meta.put("endColor", UIStyle.intParseColor(endColor));
            return this;
        }

        public void addKey(UIClick... clicks) {
            if (clicks.length > 0)
                meta.put("Keys", clicks);
        }

        public void addKey(String... keys) {
            List<WebMeta> Keys = new LinkedList<>();
            for (int i = 0; i < keys.length; i = i + 2) {
                if (i + 1 < keys.length) {
                    Keys.add(new WebMeta().put("text", keys[i]).put("value", keys[i + 1]));
                }
            }
            meta.put("Keys", Keys.toArray());

        }
    }

    protected WebMeta meta = new WebMeta();

    public UIHeader slider(Collection sliders) {
        meta.put("type", "Slider").put("data", new WebMeta().put("data", sliders));
        return this;
    }


    public UIHeader slider(UISlider... sliders) {
        meta.put("type", "Slider").put("data", new WebMeta().put("data", sliders));
        return this;
    }

    public UIHeader profile(Profile profile, String numberFormat, String amountFormat) {

        meta.put("type", "Profile").put("data", profile).put("format", new WebMeta().put("number", numberFormat).put("amount", amountFormat));
        return this;
    }

    public UIHeader profile(Profile profile) {

        meta.put("type", "Profile").put("data", profile).put("format", new WebMeta().put("number", "{number}").put("amount", "{amount}"));
        return this;
    }

    public UIHeader sliderSquare(UISlider... sliders) {
        meta.put("type", "SliderSquare").put("data", new WebMeta().put("data", sliders));
        return this;
    }

    public UIHeader portrait(Portrait discount) {
        meta.put("type", "Portrait").put("data", discount);
        return this;
    }

    public UIHeader desc(WebMeta data, String format, UIStyle style) {

        this.meta.put("type", "Desc").put("style", style).put("format", new WebMeta().put("desc", format)).put("data", data);
        return this;

    }

    public UIHeader put(String key, Object value) {
        meta.put(key, value);
        return this;
    }

    public UIHeader search(String placeholder) {

        meta.put("type", "Search").put("data", new WebMeta().put("placeholder", placeholder));

        return this;
    }

    public UIHeader search(String placeholder, String Keyword) {

        meta.put("type", "Search").put("data", new WebMeta().put("Keyword", Keyword).put("placeholder", placeholder));

        return this;
    }

    @Override
    public void write(Writer writer) {
        UMC.Data.JSON.serialize(this.meta, writer);

    }

    @Override
    public void read(String key, Object value) {

    }
}
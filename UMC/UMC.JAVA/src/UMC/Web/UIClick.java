package UMC.Web;

import UMC.Data.Utility;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;

public class UIClick implements UMC.Data.IJSON {

    public UIClick() {
    }

    public UIClick(String send) {
        this._send = send;
    }

    public UIClick(String... keys) {
        this._send = new WebMeta().put(keys);
    }

    public UIClick(WebMeta send) {
        this._send = send;
    }

    public UIClick send(WebMeta send) {
        this._send = send;
        return this;

    }

    public UIClick send(String send) {
        this._send = send;
        return this;

    }

    public UIClick send(String model, String cmd) {
        this.Model = model;
        this.Command = cmd;
        return this;
    }

    public UIClick key(String key) {
        this.Key = key;
        return this;
    }

    public UIClick text(String text) {
        this.Text = text;
        return this;
    }

    public Object send() {

        return _send;
    }

    private String Key;
    private Object _send;
    private String Model;
    private String Command;

    private String Text;
    private String Value;

    public UIClick value(String value) {
        this.Value = value;
        return this;
    }

    public String value() {
        return this.Value;
    }

    @Override
    public void write(Writer writer) {
        try {
            writer.write("{");

            if (Utility.isEmpty(this.Key) == false) {
                UMC.Data.JSON.serialize("key", writer);
                writer.write(":");
                UMC.Data.JSON.serialize(this.Key, writer);
                if (this._send != null) {

                    writer.write(",");
                    UMC.Data.JSON.serialize("send", writer);
                    writer.write(":");
                    UMC.Data.JSON.serialize(this._send, writer);


                }
            } else {

                UMC.Data.JSON.serialize("model", writer);
                writer.write(":");
                UMC.Data.JSON.serialize(this.Model, writer);
                writer.write(",");
                UMC.Data.JSON.serialize("cmd", writer);
                writer.write(":");
                UMC.Data.JSON.serialize(this.Command, writer);

                if (this._send != null) {

                    writer.write(",");
                    UMC.Data.JSON.serialize("send", writer);
                    writer.write(":");
                    UMC.Data.JSON.serialize(this._send, writer);


                }
            }

            if (Utility.isEmpty(Text) == false) {
                writer.write(",");
                UMC.Data.JSON.serialize("text", writer);
                writer.write(":");
                UMC.Data.JSON.serialize(this.Text, writer);


            }
//            if (Utility.isEmpty(Value) == false) {
//                writer.write(",");
//                UMC.Data.JSON.serialize("value", writer);
//                writer.write(":");
//                UMC.Data.JSON.serialize(this.Value, writer);
//
//
//            }
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void read(String key, Object value) {
        switch (key) {
            case "key":
                this.Key = String.valueOf(value);//value as string;
                break;
            case "send":
                this._send = value;
                break;
            case "model":
                this.Model = String.valueOf(value);//as string;
                break;
            case "cmd":
                this.Command = String.valueOf(value);
                break;
        }

    }


    public static UIClick search() {
        UIClick cl = new UIClick();
        cl.Key = "Search";
        return cl;
    }

    public static UIClick search(String type) {

        UIClick cl = new UIClick(type);
        cl.Key = "Search";
        return cl;
    }


    public static UIClick pager(String model, String cmd, WebMeta search) {
        WebMeta key = new WebMeta().put("model", model, "cmd", cmd).put("search", search);

        UIClick cl = new UIClick(key);
        cl.Key = "Pager";
        return cl;
    }

    public static UIClick pager(String model, String cmd, WebMeta search, boolean isCache) {

        WebMeta key = new WebMeta().put("model", model, "cmd", cmd).put("search", search);
        if (isCache) {
            key.put("Cache", isCache);
        }
        UIClick cl = new UIClick(key);
        cl.Key = "Pager";
        return cl;
    }

    public static UIClick pager(String model, String cmd) {
        WebMeta key = new WebMeta().put("model", model, "cmd", cmd);

        UIClick cl = new UIClick(key);
        cl.Key = "Pager";
        return cl;
    }

    public static UIClick pager(String model, String cmd, boolean isCache, String closeEvent) {
        WebMeta key = new WebMeta().put("model", model, "cmd", cmd);
        key.put("ColseEvent", closeEvent);
        if (isCache) {
            key.put("Cache", isCache);
        }

        UIClick cl = new UIClick(key);
        cl.Key = "Pager";
        return cl;
    }

    public static UIClick pager(String model, String cmd, boolean isCache) {
        WebMeta key = new WebMeta().put("model", model, "cmd", cmd);
        if (isCache) {
            key.put("Cache", isCache);
        }
        UIClick cl = new UIClick(key);
        cl.Key = "Pager";
        return cl;
    }

    public static UIClick barCode(UIClick click) {
        UIClick cl = new UIClick();
        cl.Key = "BarCode";
        cl._send = click;
        return cl;
    }

    public static UIClick pager(String model, String cmd, String... refreshEvent) {
        WebMeta key = new WebMeta().put("model", model, "cmd", cmd);
        if (refreshEvent.length > 0) {
            key.put("RefreshEvent", String.join(",", refreshEvent));
        }

        UIClick cl = new UIClick(key);
        cl.Key = "Pager";
        return cl;
    }

    public static UIClick pager(String model, String cmd, WebMeta search, String... refreshEvent) {
        WebMeta key = new WebMeta().put("model", model, "cmd", cmd).put("search", search);
        if (refreshEvent.length > 0) {
            key.put("RefreshEvent", String.join(",", refreshEvent));
        }
        UIClick cl = new UIClick(key);
        cl.Key = "Pager";
        return cl;
    }

    public static UIClick url(URI url) {
        UIClick cl = new UIClick(url.toString());
        cl.Key = "Url";
        return cl;
    }

    public static UIClick tel(String tel) {
        UIClick cl = new UIClick(tel);
        cl.Key = "Tel";
        return cl;

    }

    public static UIClick scanning() {
        UIClick cl = new UIClick();
        cl.Key = "Scanning";
        return cl;
    }

    public static UIClick scanning(UIClick click) {

        UIClick cl = new UIClick();
        cl.Key = "Scanning";
        cl._send = click;
        return cl;
    }


    public static UIClick map(String location, String address, WebMeta... items) {
        UIClick click = new UIClick(new WebMeta().put("location", location, "address", address).put("items", items));
        click.Key = "Map";
        return click;
    }

    public static UIClick map(String address, WebMeta... items) {
        if (items.length > 0) {
            UIClick click = new UIClick(new WebMeta().put("address", address).put("items", items));
            click.Key = "Map";
            return click;

        } else {
            UIClick click = new UIClick(address);
            click.Key = "Map";
            return click;
        }
    }


    public static UIClick click(UIClick click) {
        UIClick c = new UIClick();

        c.Key = "Click";

        c._send = click;
        return c;
    }

}

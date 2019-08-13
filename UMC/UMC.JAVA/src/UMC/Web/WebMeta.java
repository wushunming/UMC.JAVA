package UMC.Web;

import UMC.Data.Utility;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class WebMeta implements UMC.Data.IJSON {
    private Map<String, Object> map = new HashMap();

    public WebMeta put(String... values) {
        for (int i = 0; i < values.length; i = i + 2) {
            if (i + 1 < values.length) {
                map.put(values[i], values[i + 1]);
            }
        }
        return this;
    }

    public WebMeta(String... values) {
        if (values.length == 1) {
            map.put("type", values[0]);

        } else {
            for (int i = 0; i < values.length; i = i + 2) {
                if (i + 1 < values.length) {
                    map.put(values[i], values[i + 1]);
                }
            }
        }
    }

    public WebMeta put(String key, Object value) {

        this.map.put(key, value);

        return this;
    }

    public String get(String key) {
        Object Object = this.map.get(key);
        if (Object instanceof String) {
            return (String) Object;
        }
        return null;
    }

    public boolean containsKey(String key) {
        return this.map.containsKey(key);
    }

    public WebMeta meta(String key) {

        Object Object = this.map.get(key);
        if (Object instanceof String) {
            return null;
        }
        if (Object instanceof WebMeta) {
            return (WebMeta) Object;
        }
        if (Object instanceof Map) {
            return new WebMeta((Map) Object);
        }
        return null;
    }

    public Map<String, Object> map() {
        return map;
    }

    public void remove(String key) {
        map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public WebMeta() {
    }

    public WebMeta(WebMeta map) {
        this.map.putAll(map.map());
    }

    public WebMeta(Map map) {

        this.map.putAll(map);
    }

    @Override
    public void write(Writer writer) {
        UMC.Data.JSON.serialize(this.map, writer);

    }

    @Override
    public void read(String key, Object value) {
        this.map.put(key, value);
    }

    public WebMeta event(String name, Object value) {
        this.put("type", "UI.Event", "key", name).put("value", value);
        return this;
    }

    public WebMeta cell(UICell cell) {
        this.put("_CellName", cell.type()).put("value", cell.data()).put("format", cell.format()).put("style", cell.style());
        return this;
    }

    public WebMeta event(String name, String ui, Object value) {

        switch (Utility.isNull(ui, "none")) {
            case "none":
                this.put("type", "UI.Event", "key", name).put("value", value);
                break;
            default:
                this.put("type", "UI.Event", "key", name).put("ui", ui).put("value", value);
                break;
        }
        return this;
    }

    public WebMeta command(String model, String cmd) {

        this.put("Model", model, "Command", cmd);
        return this;
    }

    public WebMeta command(String model, String cmd, String value) {
        this.put("SendValue", value, "Model", model, "Command", cmd);
        return this;
    }

    public WebMeta command(String model, String cmd, WebMeta value) {
        this.put("Model", model, "Command", cmd).put("SendValue", value);
        return this;
    }

    public WebMeta closeEvent(String... values) {
        this.put("CloseEvent", String.join(",", values));
        return this;
    }

    public WebMeta refreshEvent(String... values) {
        this.put("RefreshEvent", String.join(",", values));
        return this;
    }

    public WebMeta placeholder(String placeholder) {
        this.put("placeholder", placeholder);
        return this;
    }


}

package UMC.Web;


import UMC.Data.Utility;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIDataSource implements UMC.Data.IJSON {
    Map _data = new HashMap();

    public UIDataSource(List<Map> data, String cell) {
        _data.put("data", data);
        if (Utility.isEmpty(cell) == false) {
            _data.put("cell", cell);
        }
    }

    public UIDataSource text(String text) {
        this._data.put("text", text);
        return this;
    }

    public UIDataSource(String model, String cmd) {
        this(model, cmd, "");
    }

    public UIDataSource(String model, String cmd, String cell) {
        if (Utility.isEmpty(model) == false && Utility.isEmpty(cmd) == false) {
            _data.put("model", model);
            _data.put("cmd", cmd);
        }
        if (Utility.isEmpty(cell) == false) {
            _data.put("cell", cell);
        }
    }

    public UIDataSource(String model, String cmd, WebMeta search, String cell) {
        this(model, cmd, cell);


        if (search != null && search.size() > 0) {
            _data.put("search", search);
        }
    }

    public void buildStyle(WebMeta style) {
        _style.copy(new UIStyle(style));
    }

    UIStyle _style = new UIStyle();

    public UIStyle style()

    {

        return _style;

    }

    public void buildStyle(UIStyle style) {
        _style.copy(style);
    }

    public void buildFormat(WebMeta format) {
        if (format != null && format.size() > 0) {
            _data.put("format", format);
        }
    }

    public void buildSubmit(String model, String cmd, String send) {
        WebMeta click = new UMC.Web.WebMeta().put("model", model).put("cmd", cmd);
        if (Utility.isEmpty(send) == false) {
            click.put("send", send);
            ;
        }

        _data.put("submit", click);

    }

    public void buildSubmit(String model, String cmd, WebMeta send) {
        WebMeta click = new UMC.Web.WebMeta().put("model", model).put("cmd", cmd);
        if (send != null && send.size() > 0) {
            click.put("send", send);
            ;
        }
        _data.put("submit", click);

    }

    public void buildClick(String model, String cmd, String send) {
        WebMeta click = new UMC.Web.WebMeta().put("model", model).put("cmd", cmd);
        if (Utility.isEmpty(send) == false) {
            click.put("send", send);
            ;
        }

        _data.put("click", click);

    }

    public void buildClick(String model, String cmd, WebMeta send) {
        WebMeta click = new UMC.Web.WebMeta().put("model", model).put("cmd", cmd);
        if (send != null && send.size() > 0) {
            click.put("send", send);
            ;
        }
        _data.put("click", click);

    }

    @Override
    public void write(Writer writer) {

        _data.put("style", _style);
        UMC.Data.JSON.serialize(_data, writer);
    }

    @Override
    public void read(String key, Object value) {

    }


}
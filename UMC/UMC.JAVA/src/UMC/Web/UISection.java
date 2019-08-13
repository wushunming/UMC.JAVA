package UMC.Web;


import UMC.Data.Utility;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class UISection implements UMC.Data.IJSON {

    public static class Editer {
        private UMC.Web.WebMeta webMeta = new WebMeta();

        /**
         * @param section UISection 中的section
         * @param row UISection 中的row
         */
        public Editer(int section, int row) {

            webMeta.put("section", section).put("row", row);

        }


        /**行更新，
         * @param value 更新的组件
         * @param reloadSinge 是否是单行更新，还是整体更新
         * @return
         */
        public Editer put(UICell value, boolean reloadSinge) {
            if (reloadSinge) {
                webMeta.put("value", new WebMeta().cell(value)).put("method", "PUT").put("reloadSinle", true);
            } else {
                webMeta.put("value", new WebMeta().cell(value)).put("method", "PUT");

            }
            return this;
        }


        /**行删
         * @return
         */
        public Editer delete() {
            webMeta.put("method", "DEL");
            return this;
        }
        /**行追加
         * @param value 追加的组件
         * @return
         */
        public Editer append(UICell value) {
            webMeta.put("value", new WebMeta().cell(value)).put("method", "APPEND");
            return this;
        }
        /**行插入
         * @param value 插入的组件
         * @return
         */
        public Editer insert(UICell value) {
            webMeta.put("value", new WebMeta().cell(value)).put("method", "INSERT");
            return this;
        }

        /**发送到客户端
         * @param context UMC上下文
         * @param ui 界面的名
         * @param endResponse  是否立刻返回客户端
         */
        public void builder(WebContext context, String ui, boolean endResponse) {
            context.send(new UMC.Web.WebMeta().event("UI.Edit", ui, webMeta), endResponse);

        }
    }

    WebMeta _header = new WebMeta();

    private UISection() {

    }

    public WebMeta header() {
        return _header;

    }

    UIHeader _uiheaders;
    UIFooter _uifooter;
    UITitle _title;

    public UIHeader uiHeader() {
        return _uiheaders;
    }

    public UISection title(UITitle title) {
        _title = title;
        return this;
    }

    public UITitle title() {
        return _title;
    }


    public UISection uiFooter(UIFooter footer) {
        _uifooter = footer;
        return this;
    }

    public UIFooter uiFooter() {
        return _uifooter;
    }

    public UISection uiheader(UIHeader header) {
        _uiheaders = header;
        return this;
    }

    public static UISection create(UIHeader header, UIFooter footer) {
        UISection t = new UISection();
        t.Sections = new LinkedList<>();
        t.Sections.add(t);
        WebMeta meta = new WebMeta();
        t._uiheaders = header;
        t._uifooter = footer;
        return t;
    }


    public static UISection create(UIHeader header, UIFooter footer, UITitle title) {
        UISection t = new UISection();
        t.Sections = new LinkedList<>();
        t.Sections.add(t);
        WebMeta meta = new WebMeta();
        t._uiheaders = header;
        t._uifooter = footer;
        t._title = title;
        return t;
    }

    public static UISection create(UITitle title, UIFooter footer) {
        UISection t = new UISection();
        t.Sections = new LinkedList<>();
        t.Sections.add(t);
        WebMeta meta = new WebMeta();
        t._uifooter = footer;
        t._title = title;
        return t;
    }

    public static UISection create(UITitle title) {
        UISection t = new UISection();
        t.Sections = new LinkedList<>();
        t.Sections.add(t);
        t._title = title;
        WebMeta meta = new WebMeta();
        return t;


    }

    public static UISection create(UIHeader header, UITitle title) {
        UISection t = new UISection();
        t.Sections = new LinkedList<>();
        t.Sections.add(t);
        t._title = title;
        WebMeta meta = new WebMeta();
        t._uiheaders = header;
        return t;


    }

    private int Total;

    public int total() {
        return Total;
    }

    public UISection total(int total) {
        Total = total;
        return this;
    }

    public static UISection create() {
        UISection t = new UISection();
        t.Sections = new LinkedList<>();
        t.Sections.add(t);
        return t;

    }

    public String Key;

    public boolean IsEditer;

    List<UISection> Sections;
    private Object _data;
    List<WebMeta> data = new LinkedList<>();

    public UISection newSection() {
        UISection t = new UISection();
        t.Sections = this.Sections;
        this.Sections.add(t);
        return t;
    }

    public int size()

    {

        return this.Sections.size();

    }

    public UISection newSection(Collection data) {
        UISection t = new UISection();
        t.Sections = this.Sections;
        this.Sections.add(t);
        t._data = data;
        return t;
    }

    public UISection putCells(WebMeta... data) {

        this.data.addAll(Arrays.asList(data));
        return this;
    }

    public UISection putCells(UICell... cells) {
        for (int c = 0; c < cells.length; c++)//var sec in this.Sections)
        {
            this.put(cells[c]);
        }
        return this;
    }

    public int length()

    {
        return data.size();

    }

    public UISection putCell(String text, String value, UIClick click) {
        return this.put(UICell.create("Cell", new WebMeta().put("value", value, "text", text).put("click", click)));

    }

    public UISection putCell(String text, String value, String click) {
        return this.put(UICell.create("Cell", new WebMeta().put("value", value, "text", text).put("click", click)));

    }

    public UISection putCell(String text, UIClick click) {
        return this.put(UICell.create("Cell", new WebMeta().put("text", text).put("click", click)));

    }

    public UISection putCell(String text, String value) {
        return this.put(UICell.create("Cell", new WebMeta().put("value", value, "text", text)));

    }

    public UISection putCell(char icon, String text, String value) {
        return this.put(UICell.create("UI", new WebMeta().put("value", value, "text", text).put("Icon", icon)));

    }

    public UISection putCell(char icon, String text, String value, UIClick click) {
        return this.put(UICell.create("UI", new WebMeta().put("value", value, "text", text).put("Icon", icon).put("click", click)));

    }

    /**添加支持左滑删除的组件
     * @param cell 行组件
     * @param eventText 删除后请求的事件
     * @return
     */
    public UISection delete(UICell cell, UIEventText eventText) {

        data.add(new WebMeta().put("del", eventText).put("_CellName", cell.type()).put("value", cell.data()).put("format", cell.format()).put("style", cell.style()));
        return this;
    }

    public UISection put(UICell cell) {

        data.add(new WebMeta().put("_CellName", cell.type()).put("value", cell.data()).put("format", cell.format()).put("style", cell.style()));
        return this;
    }

    public UISection put(String type, WebMeta value, WebMeta format, UIStyle style) {
        data.add(new WebMeta().put("_CellName", type).put("value", value).put("format", format).put("style", style));
        return this;
    }

    public UISection putPro(UIPrice... pros) {
        data.add(new WebMeta().put("_CellName", "Products").put("value", new WebMeta().put("data", pros)));
        return this;

    }

    public UISection putPro(UIStyle style, UIPrice... pros) {
        data.add(new WebMeta().put("_CellName", "Products").put("value", new WebMeta().put("data", pros)).put("style", style));
        return this;
    }

    public UISection putItems(String model, UIItem... items) {
        data.add(new WebMeta().put("_CellName", "UIItems").put("value", new WebMeta().put("items", items).put("model", model)));//.put("format", format).put("style", style));
        return this;
    }

    public UISection putItems(UIItem... items) {
        data.add(new WebMeta().put("_CellName", "UIItems").put("value", new WebMeta().put("items", items)));//.put("format", format).put("style", style));
        return this;
    }

    public UISection putItems(UIStyle style, UIItem... items) {
        data.add(new WebMeta().put("_CellName", "UIItems").put("value", new WebMeta().put("items", items)).put("style", style));
        return this;
    }

    public UISection putIcon(UIEventText... icons) {
        data.add(new WebMeta().put("_CellName", "Icons").put("value", new WebMeta().put("icons", icons)));//.put("format", format).put("style", style));
        return this;
    }

    public UISection putIcon(UIStyle style, UIEventText... icons) {
        data.add(new WebMeta().put("_CellName", "Icons").put("value", new WebMeta().put("icons", icons)).put("style", style));
        return this;
    }

    public UISection put(String type, WebMeta value) {
        data.add(new WebMeta().put("_CellName", type).put("value", value));
        return this;
    }

    public UISection putSlider(UISlider... sliders) {
        data.add(new WebMeta().put("_CellName", "Slider").put("value", new WebMeta().put("data", sliders)));
        return this;
    }


    public Boolean IsNext;

    public UISection start(boolean IsNext) {
        this.IsNext = IsNext;
        return this;
    }

    public Integer StartIndex;

    public UISection start(int start) {
        this.StartIndex = start;
        return this;
    }

    public UISection putNumberCell(String text, String value, UIClick submit) {
        UICell cell = UICell.create("NumberCell", new UMC.Web.WebMeta().put("text", text, "value", value).put("submit", submit));
        this.put(cell);
        return this;
    }

    public UISection putNumberCell(String text, String value, String title, UIClick submit) {
        UICell cell = UICell.create("NumberCell", new UMC.Web.WebMeta().put("text", text, "value", value, "title", title).put("submit", submit));
        this.put(cell);
        return this;
    }

    public UISection putImageTextValue(String src, String value, UIClick click) {
        UICell cell = UICell.create("ImageTextValue", new UMC.Web.WebMeta().put("src", src, "value", value).put("click", click));
        this.put(cell);
        return this;
    }

    public UISection putImageTextValue(String src, String text, String value, UIClick click) {
        UICell cell = UICell.create("ImageTextValue", new UMC.Web.WebMeta().put("src", src, "value", value, "text", text).put("click", click));
        this.put(cell);
        return this;
    }

    public UISection putImageTextValue(String src, String value, int imageWidth, UIClick click) {
        UICell cell = UICell.create("ImageTextValue", new UMC.Web.WebMeta().put("src", src, "value", value).put("click", click));
        cell.style().name("image-width", imageWidth);
        this.put(cell);
        return this;
    }

    public UISection putImageTextValue(String src, String text, String value, int imageWidth, UIClick click) {
        UICell cell = UICell.create("ImageTextValue", new UMC.Web.WebMeta().put("src", src, "value", value, "text", text).put("click", click));
        cell.style().name("image-width", imageWidth);
        this.put(cell);
        return this;
    }


    @Override
    public void read(String key, Object value) {

    }

    @Override
    public void write(Writer writer) {
        try {
            writer.write("{");

            if (_uiheaders != null) {
                UMC.Data.JSON.serialize("Header", writer);
                writer.write(":");
                UMC.Data.JSON.serialize(this._uiheaders, writer);
                writer.write(",");
            }
            if (this._title != null) {
                UMC.Data.JSON.serialize("Title", writer);
                writer.write(":");
                UMC.Data.JSON.serialize(this._title, writer);

                writer.write(",");

            }
            if (_uifooter != null) {
                UMC.Data.JSON.serialize("Footer", writer);
                writer.write(":");
                UMC.Data.JSON.serialize(this._uifooter, writer);

                writer.write(",");
            }
            if (Total > 0) {
                UMC.Data.JSON.serialize("total", writer);
                writer.write(":");
                UMC.Data.JSON.serialize(Total, writer);
                writer.write(",");

            }
            if (StartIndex != null && StartIndex > -1) {
                UMC.Data.JSON.serialize("start", writer);
                writer.write(":");
                UMC.Data.JSON.serialize(this.StartIndex, writer);
                writer.write(",");

            }
            if (this.IsNext != null) {

                UMC.Data.JSON.serialize("next", writer);
                writer.write(":");
                UMC.Data.JSON.serialize(this.IsNext, writer);
                writer.write(",");
            }
            UMC.Data.JSON.serialize("DataSource", writer);
            writer.write(":[");
            boolean b = false;
            for (int c = 0; c < this.Sections.size(); c++)//var sec in this.Sections)
            {
                UISection sec = this.Sections.get(c);
                if (b) {
                    writer.write(",");
                } else {
                    b = true;
                }
                writer.write("{");
                if (Utility.isEmpty(sec.Key) == false) {
                    UMC.Data.JSON.serialize("key", writer);
                    writer.write(":");
                    UMC.Data.JSON.serialize(sec.Key, writer);
                    writer.write(",");
                }
                if (sec.IsEditer) {
                    UMC.Data.JSON.serialize("isEditer", writer);
                    writer.write(":");
                    UMC.Data.JSON.serialize(sec.IsEditer, writer);
                    writer.write(",");

                }
                UMC.Data.JSON.serialize("data", writer);
                writer.write(":");
                if (sec._data != null) {

                    UMC.Data.JSON.serialize(sec._data, writer);
                } else {
                    UMC.Data.JSON.serialize(sec.data, writer);
                }
                if (sec._header.size() > 0) {
                    writer.write(",");
                    UMC.Data.JSON.serialize("header", writer);
                    writer.write(":");
                    UMC.Data.JSON.serialize(sec._header, writer);
                }
                writer.write("}");

            }

            writer.write("]}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
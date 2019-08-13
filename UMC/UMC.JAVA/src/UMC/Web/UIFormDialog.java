package UMC.Web;

import UMC.Data.Utility;

import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UIFormDialog extends UIDialog {
  private   List<WebMeta> dataSrouce = new LinkedList<>();

    @Override
    protected String type() {
        return "Form";
    }

    private WebMeta meta(String type, String name, String title) {
        WebMeta v = new WebMeta();
        v.put("Type", type).put("Name", name)
                .put("Title", title);
        this.dataSrouce.add(v);
        return v;
    }


    private WebMeta meta(String type, String name, String title, String defaultValue) {


        return meta(type, name, title).put("DefaultValue", defaultValue);
    }


    public void menu(String text, String model, String cmd, String value) {
        this.menu(this.createMenu(text, model, cmd, value));
    }

    public void menu(WebMeta... menus) {
        this.config.put("menu", menus);
    }


    public void menu(String text, String model, String cmd, WebMeta param) {
        this.menu(this.createMenu(text, model, cmd, param));
    }

    public void add(String type, WebMeta value, WebMeta format, UIStyle style) {
        this.dataSrouce.add(new WebMeta().put("Type", type).put("value", value).put("format", format).put("style", style));

    }

    public void add(UICell cell) {
        this.dataSrouce.add(new WebMeta().put("Type", cell.type()).put("value", cell.data()).put("format", cell.format()).put("style", cell.style()));

    }

    public void addArea(String title, String Code, String defaultValue) {

        this.addOption(title, Code, defaultValue, defaultValue).command("Schedule", "Area");
    }

    public void addSlider(String title, String Code, int defaultValue, int min, int max) {

        meta("FieldSlider", Code, title).put("Max", max).put("Min", min).put("DefaultValue", defaultValue);
    }

    public void addSlider(String title, String Code, int defaultValue) {
        addSlider(title, Code, defaultValue, 0, 100);
    }

    public void addAddress(String title, String Code, String defaultValue) {

        meta("Address", Code, title).put("DefaultValue", defaultValue);
    }

    public void addPhone(String title, String Code, String defaultValue) {

        meta("Number", Code, title, defaultValue).put("Vtype", "Phone");
    }

    public WebMeta addNumber(String title, String Code, Integer defaultValue) {

        return meta("Number", Code, title, defaultValue + "");//.put("Vtype", "Phone");
    }

    public WebMeta add(String type, String Code, String title, String defaultValue) {

        return meta(type, Code, title, defaultValue);

    }

    public void addNumber(String title, String Code, Float defaultValue) {
        meta("Number", Code, title, defaultValue + "");
    }


    public void addConfirm(String caption) {
        this.addConfirm(caption, "CONFIRM_NAME", "YES");
    }

    public void addConfirm(String caption, String name, String defaultValue) {
        meta("Confirm", name, null, defaultValue).put("Text", caption);
    }

    public void addPrompt(String caption) {
        meta("Prompt", Math.random() + "", null).put("Text", caption);
    }

    public WebMeta addBarCode(String title, String Code, String defaultValue) {

        return meta("BarCode", Code, title, defaultValue);
    }

    public WebMeta addOption(String title, String code, String value, String text) {
        return meta("Option", code, title, value).put("Text", text);
    }

    public WebMeta addFile(String title, String Code, String defaultValue) {

        return meta("File", Code, title, defaultValue);//.put("Text", text);
    }

    public WebMeta addFiles(String title, String Code) {
        return meta("Files", Code, title);// defaultValue);/
    }

    public WebMeta addTextarea(String title, String Code, String defaultValue) {

        return meta("Textarea", Code, title, defaultValue);
    }

    public WebMeta addDate(String title, String code, Date date) {

        WebMeta v = meta("Date", code, title);
        if (date != null) {
            java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            v.put("DefaultValue", format.format(date));
        }
        return v;
    }

    public WebMeta addText(String title, String code, String defaultValue) {

        return meta("Text", code, title, defaultValue);
    }

    public WebMeta addTextValue(String title, ListItemCollection items) {
        WebMeta v = new WebMeta();
        if (Utility.isEmpty(title) == false)
            v.put("Title", title);

        v.put("DataSource", items);
        v.put("Type", "TextValue");

        this.dataSrouce.add(v);
        return v;
    }

    public WebMeta addTextValue(ListItemCollection items) {
        return this.addTextValue("", items);
    }

    public WebMeta addTextNameValue(ListItemCollection items) {
        return this.addTextNameValue("", items);
    }

    public WebMeta addTextNameValue(String title, ListItemCollection items) {

        WebMeta v = new WebMeta();
        if (Utility.isEmpty(title) == false)
            v.put("Title", title);

        v.put("DataSource", items);
        v.put("Type", "TextNameValue");

        this.dataSrouce.add(v);
        return v;
    }

    public ListItemCollection addTextNameValue(String title) {
        ListItemCollection t = new ListItemCollection();
        this.addTextNameValue(title, t);
        return t;
    }

    public ListItemCollection addTextNameValue() {
        ListItemCollection t = new ListItemCollection();
        this.addTextNameValue(t);
        return t;
    }

    public ListItemCollection addTextValue(String title) {
        ListItemCollection t = new ListItemCollection();
        this.addTextValue(title, t);
        return t;
    }

    public ListItemCollection addTextValue() {
        ListItemCollection t = new ListItemCollection();
        this.addTextValue(t);
        return t;
    }

    public void addImage(URI src) {
        WebMeta v = new WebMeta();
        v.put("Type", "Image");
        v.put("Src", src.toString());
        v.put("Name", "_image_" + this.dataSrouce.size());

        this.dataSrouce.add(v);
    }


    public WebMeta addPassword(String title, String Code, boolean IsDisabledMD5) {
        WebMeta v = meta("Password", Code, title);

        if (IsDisabledMD5) {
            v.put("IsDisabledMD5", "true");

        } else {
            v.put("Time", System.currentTimeMillis() / 1000);
        }
        return v;
    }

    public WebMeta addPassword(String title, String Code, String defaultValue) {
        WebMeta v = meta("Password", Code, title, defaultValue);

        v.put("Time", System.currentTimeMillis() / 1000);

        return v;
    }

    public WebMeta addUI(String title, String name, String desc) {
        WebMeta v = meta("UI", name, title, desc);

        return v;

    }

    public WebMeta addUI(String title, String desc) {
        return addUI(title, "UI" + this.dataSrouce.size(), desc);
    }

    public WebMeta addUIIcon(char icon, String title) {
        return addUIIcon(icon, title, "", 0);
    }

    public WebMeta addUIIcon(char icon, String title, int color) {
        return addUIIcon(icon, title, "", color);
    }

    public WebMeta addUIIcon(char icon, String title, String desc, int color) {
        WebMeta v = meta("UI", "icon" + this.dataSrouce.size(), title, desc);
        v.put("Icon", icon);

        if (color != 0) {
            v.put("Color", UIStyle.intParseColor(color));
        }
        return v;
    }


    public WebMeta addTime(String title, String code, int hour, int minute) {
        WebMeta v = meta("Time", code, title, hour + ":" + minute);
        return v;

    }

    public WebMeta addTime(String title, String code, Date defaultValue) {
        WebMeta v = meta("Time", code, title);
        if (defaultValue != null) {
            java.text.DateFormat format = new java.text.SimpleDateFormat("HH:mm");
            v.put("DefaultValue", format.format(defaultValue));
        }
        return v;
    }

    public ListItemCollection addSelect(String title, String code) {
        ListItemCollection t = new ListItemCollection();
        addSelect(title, code, t);
        return t;
    }

    public void addSelect(String title, String code, ListItemCollection items) {
        WebMeta v = meta("Select", code, title);

        v.put("DataSource", items);
        this.dataSrouce.add(v);
    }

    public ListItemCollection addCheckBox(String title, String code, String defaultValue) {
        ListItemCollection t = new ListItemCollection();
        addCheckBox(title, code, t, defaultValue);
        return t;
    }

    public ListItemCollection addCheckBox(String title, String code) {
        ListItemCollection t = new ListItemCollection();
        addCheckBox(title, code, t);
        return t;
    }

    public void addCheckBox(String title, String code, ListItemCollection items, String defaultValue) {

        WebMeta v = meta("CheckboxGroup", code, title, defaultValue);

        v.put("DataSource", items);
    }

    public void addCheckBox(String title, String code, ListItemCollection items) {
        addCheckBox(title, code, items, null);
    }

    public ListItemCollection addRadio(String title, String code) {
        ListItemCollection t = new ListItemCollection();
        addRadio(title, code, t);
        return t;
    }

    public void addRadio(String title, String code, ListItemCollection items) {
        WebMeta v = meta("RadioGroup", code, title);
        v.put("DataSource", items);

    }

    public void submit(String btnName) {
        this.config.put("submit", btnName);
        this.dataSrouce.get(this.dataSrouce.size() - 1).put("Submit", "YES");
    }

    public void submit(String btnName, String model, String cmd, WebMeta param) {
        WebMeta p = new WebMeta();
        if (param != null && param.size() > 0) {
            p.put("send", param);
        }
        p.put("model", model, "cmd", cmd);


        if (Utility.isEmpty(btnName) == false) {
            p.put("text", btnName);
        }

        this.config.put("submit", p);
        this.dataSrouce.get(this.dataSrouce.size() - 1).put("Submit", "YES");

    }

    private WebMeta submit;

    public void submit(String btnName, String model, String cmd, String... colseEvent) {
        WebMeta p = new WebMeta();

        p.put("model", model, "cmd", cmd);
        if (Utility.isEmpty(btnName) == false) {
            p.put("text", btnName);
        }
        if (colseEvent.length > 0) {
            this.config.put("CloseEvent", String.join(",", colseEvent));
        }
        this.config.put("submit", p);

    }

    public void hideSubmit() {
        this.config.put("submit", false);
    }

    public void submit() {
        this.dataSrouce.get(this.dataSrouce.size() - 1).put("Submit", "YES");
    }

    public WebMeta addVerify(String title, String code, String placeholder) {


        WebMeta v = meta("Verify", code, title).put("placeholder", placeholder);
        return v;

    }

    public void submit(String btnName, WebRequest request, String... colseEvent) {
        if (colseEvent.length > 0) {
            this.config.put("CloseEvent", String.join(",", colseEvent));
        }
        WebMeta pa = new WebMeta(request.arguments());

        submit = new WebMeta().put("model", request.model(), "cmd", request.cmd(), "text", btnName).put("send", pa);
        submit(btnName);
    }

    protected void initialization(WebContext context) {
        if (submit != null) {
            WebMeta send = submit.meta("send");
            send.put(UIDialog.KEY_DIALOG_ID, this.asyncId());
            //submit._send !=
            this.config.put("submit", submit);
        }
        this.config.put("DataSource", dataSrouce);
    }


}
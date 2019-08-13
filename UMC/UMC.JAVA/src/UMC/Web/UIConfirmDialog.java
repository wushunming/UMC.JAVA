package UMC.Web;


import UMC.Data.Utility;

public class UIConfirmDialog extends UIDialog {
    public UIConfirmDialog(String text) {
        super();
        this.title("提示");
        this.config("Text", text);
        this.config("DefaultValue", "YES");
    }


    public UIConfirmDialog(String text, String defaultValue) {
        super();
        this.config.put("Text", text);
        this.config("DefaultValue", defaultValue);
        if (Utility.isEmpty(defaultValue)) {

            this.config("DefaultValue", "YES");
        } else {

            this.config("DefaultValue", defaultValue);
        }
    }


    @Override
    protected String type() {
        return "Confirm";
    }
}
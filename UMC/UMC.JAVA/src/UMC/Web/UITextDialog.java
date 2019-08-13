package UMC.Web;

public class UITextDialog extends UIDialog {
    public UITextDialog(String DefaultValue) {
        super();
        this.config.put("DefaultValue", DefaultValue);

    }

    public UITextDialog() {
        super();
    }

    @Override
    protected String type() {
        return "Text";
    }
}

package UMC.Web;

public class UIBarCodeDialog extends UIDialog {
    public UIBarCodeDialog() {
    }

    public UIBarCodeDialog(String defaultValue) {
        super();
        this.config.put("DefaultValue", defaultValue);
    }

    @Override
    protected String type() {
        return "BarCode";
    }

}

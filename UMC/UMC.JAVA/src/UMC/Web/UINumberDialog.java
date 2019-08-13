package UMC.Web;


public class UINumberDialog extends UIDialog {
    public UINumberDialog(Float defaultValue) {
        this.config.put("DefaultValue", defaultValue);
    }

    public UINumberDialog(String defaultValue) {
        this.config.put("DefaultValue", defaultValue);
    }

    public UINumberDialog() {
    }

    @Override
    protected String type() {

        return "Number";
    }


}
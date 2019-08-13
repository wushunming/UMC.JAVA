package UMC.Web;


public class UICheckboxDialog extends UISelectDialog {
    public UICheckboxDialog(){
        super();
    }
    @Override
    protected String type() {
        return "CheckboxGroup";
    }
    public UICheckboxDialog(String defaultValue) {
        this.config.put("DefaultValue", defaultValue);
    }

}
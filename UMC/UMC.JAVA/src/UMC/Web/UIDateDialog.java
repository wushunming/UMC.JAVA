package UMC.Web;

import java.util.Date;

public class UIDateDialog extends UIDialog {
    public UIDateDialog(Date date) {
        super();
        if (date != null) {
            java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            this.config.put("DefaultValue", format.format(date));
        }
    }

    public UIDateDialog() {
    }

    @Override
    protected String type() {
        return "Date";
    }
}

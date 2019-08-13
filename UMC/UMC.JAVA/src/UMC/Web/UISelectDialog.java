package UMC.Web;


public class UISelectDialog extends UIDialog {
    ListItemCollection _nSource = new ListItemCollection();


    public ListItemCollection options() {
        return _nSource;
    }


    protected void initialization(WebContext context) {


        this.config.put("DataSource", _nSource);
        super.initialization(context);
    }

    @Override
    protected String type() {
        return "Select";
    }



}
package UMC.Web;

import java.util.LinkedList;
import java.util.List;

public class UISheetDialog extends UIDialog {
    List<UIClick> _nSource = new LinkedList<>();


    /// <summary>
    /// 文本对话框选择配置
    /// </summary>
    public List<UIClick> options() {

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

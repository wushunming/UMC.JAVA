package UMC.Web;


import UMC.Data.IJSON;
import UMC.Data.Utility;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;

public abstract class UIDialog {


    public interface Callback {
        UIDialog callback(String asyncId);
    }

    private static class UIFromValue extends UIFormDialog {


        protected WebMeta InputValue;

        @Override
        protected String type() {
            return null;
        }
    }

    private static class UIDialogValue extends UIDialog {


        protected String InputValue;

        @Override
        protected String type() {
            return null;
        }
    }

    private static class UIDialoger extends UIDialog {

        public UIDialoger(String type) {
            this._DType = type;
        }

        String _DType;

        @Override
        protected String type() {
            return _DType;
        }
    }


    public static UIDialog createDialog(String type) {
        return new UIDialoger(type);
    }

    public static UIDialog createImage(String title, URI uri, String tip) {
        UIDialoger p = new UIDialoger("Image");
        p.title(title).config.put("Url", uri.toString()).put("Text", tip);

        return p;
    }

    public WebMeta createMenu(String text, String model, String cmd, String value) {
        WebMeta p = new WebMeta();
        p.put("model", model).put("text", text)
                .put("cmd", cmd);
        if (Utility.isEmpty(value) == false) {
            p.put("send", value);
        }
        return p;
    }

    public static WebMeta createMenu(String text, String model, String cmd, WebMeta param) {

        WebMeta p = new WebMeta();
        p.put("model", model).put("text", text)
                .put("cmd", cmd)
                .put("send", param);
        return p;

    }


    private String _asyncId;

    protected String asyncId() {
        return _asyncId;
    }

    public static UIDialog returnValue(String value) {
        UIDialogValue v = new UIDialogValue();

        v.InputValue = value;//as string;
        return v;

    }

    public static UIFormDialog returnValue(WebMeta value) {
        UIFromValue v = new UIFromValue();

        v.InputValue = value;// as WebMeta;

        return v;

    }

    public static WebMeta asyncDialog(String asyncId, WebContext context, Callback callback) {

        return (WebMeta) asyncValue(context, asyncId, false, callback, false);
    }


    /// <summary>
    /// 对话框类型
    /// </summary>
    protected abstract String type();

    /// <summary>
    /// 默认值
    /// </summary>
    public UIDialog value(String value) {
        config.put("DefaultValue");
        return this;
    }


    public UIDialog refreshEvent(String... event) {
        config.put("RefreshEvent", String.join(",", event));
        return this;
    }


    public UIDialog closeEvent(String... event) {
        config.put("CloseEvent", String.join(",", event));
        return this;
    }

    public UIDialog title(String title) {
        config.put("Title", title);
        return this;
    }

    public UIDialog config(String name, String value) {
        config.put(name, value);
        return this;
    }

    public UIDialog config(String name, IJSON value) {
        config.put(name, value);
        return this;
    }

    protected WebMeta config = new WebMeta();


    public static String asyncDialog(WebContext context, String asyncId, UIDialog dialog) {
        return asyncDialog(context, asyncId, l -> dialog, false);
    }

    public static String asyncDialog(WebContext context, String asyncId, Callback callback) {
        return asyncDialog(context, asyncId, callback, false);
    }


    public static String asyncDialog(WebContext context, String asyncId, Callback callback, boolean IsDialogValue) {
        return (String) asyncValue(context, asyncId, true, callback, IsDialogValue);
    }

    public static final String KEY_DIALOG_ID = "KEY_DIALOG_ID";
    final static String AsyncDialog = "AsyncDialog";
    final static String Dialog = "Dialog";

    private static Object asyncValue(WebContext context, String asyncId, boolean singleValue, Callback callback, boolean IsDialog) {

        WebRequest request = context.request();
        WebResponse response = context.response();
        if (singleValue) {
            String rValue = request.arguments().get(asyncId);
            if (Utility.isEmpty(rValue)) {
                String value = request.headers().get(Dialog);
                boolean isSVs = false;

                if (Utility.isEmpty(value) && request.headers().containsKey(Dialog)) {
                    WebMeta meValue = request.headers().meta(Dialog);
                    if (meValue != null) {
                        value = meValue.get(asyncId);
                    } else {
                        value = request.headers().get(Dialog);
                    }
                }
                if (Utility.isEmpty(value)) {
                    if (IsDialog == false) {
                        if (context.items().containsKey(context.activity()) == false) {
                            value = request.sendValue();
                        }
                        if (Utility.isEmpty(value)) {
                            Object obj = request.headers().map().get(request.model());

                            if (obj instanceof WebMeta) {
                                WebMeta mob = ((WebMeta) obj);
                                value = mob.get(asyncId);
                                mob.remove(asyncId);
                                isSVs = true;
                            } else if (obj instanceof Map) {
                                Map<String, String> idc = ((Map) obj);
                                value = idc.get(asyncId);
                                idc.remove(asyncId);//asyncId);
                                isSVs = true;
                            }
                        }
                    }

                }

                if (Utility.isEmpty(value) == false) {
                    if (isSVs) {
                        request.arguments().put(asyncId, value);
                        return value;
                    } else if (context.items().containsKey(context.activity()) == false) {
                        context.items().put(context.activity(), true);
                        request.arguments().put(asyncId, value);
                        return value;
                    }
                }
                UIDialog dialog = callback.callback(asyncId);
                dialog._asyncId = asyncId;//asyncId();
                if (dialog instanceof UIDialogValue) {
                    value = ((UIDialogValue) dialog).InputValue;

                    request.arguments().put(asyncId, value);
                    return value;
                }
                dialog.initialization(context);

                redirectDialog(request.model(), request.cmd(), dialog, response, request);
            }
            return rValue;
        } else {
            WebMeta rValue = request.arguments().meta(asyncId);
            if (rValue == null) {
                rValue = request.headers().meta(Dialog);//?? request.SendValues;

                if (rValue == null) {
                    WebMeta sendValue = request.sendValues();
                    if (sendValue != null && sendValue.containsKey(KEY_DIALOG_ID)) {
                        if (sendValue.get(KEY_DIALOG_ID).equals(asyncId)) {
                            rValue = new WebMeta(sendValue.map());
                            rValue.remove(KEY_DIALOG_ID);
                            Iterator<String> em = request.arguments().map().keySet().iterator();
                            while (em.hasNext()) {
                                rValue.remove(em.next());
                            }
                        }
                    }
                }

                if (rValue == null || context.items().containsKey(context.activity())) {
                    UIDialog dialog = callback.callback(asyncId);
                    dialog._asyncId = asyncId;//();
                    if (dialog instanceof UIFromValue) {
                        rValue = ((UIFromValue) dialog).InputValue;
                        request.arguments().put(asyncId, rValue);
                        return rValue;
                    }
                    dialog.initialization(context);
                    redirectDialog(request.model(), request.cmd(), dialog, response, request);
                } else {
                    context.items().put(context.activity(), true);
                    request.arguments().put(asyncId, rValue);
                }
            }
            return rValue;
        }
    }

    protected void initialization(WebContext context) {
    }


    private static void redirectDialog(String mode, String cmd, UIDialog dialog, WebResponse response, WebRequest req) {
        WebMeta items = req.items();
        WebMeta arguments = req.arguments();
        if (items.size() > 0) {
            arguments.put(WebRequest.KEY_ARGUMENTS_ITEMS, items);

        }
        dialog.config.put("Type", dialog.type());
        response.headers().put(AsyncDialog, dialog.config)
                .put(WebRequest.KEY_HEADER_ARGUMENTS, arguments);
        response.ClientEvent |= WebEvent.ASYNCDIALOG | WebEvent.DIALOG;
        response.redirect(mode, cmd, true);


    }


}
package UMC.Web;


import UMC.Data.Utility;

import java.util.*;

public abstract class UIGridDialog extends UIDialog {

    private static class aGridDialog extends UIGridDialog {
        Header header;

        public aGridDialog(Header header, Object data) {
            this.setAsyncData(true);// = true;
            this.header = header;
            this.data = data;
        }

        Object data;


        @Override
        protected Map getHeader() {
            return header.getHeaderMap();
        }

        @Override
        protected Map getData(Map paramsKey) {
            Map hash = new Hashtable();
            hash.put("data", data);
            return hash;
        }
    }


    public static UIGridDialog create(UIGridDialog.Header header, boolean isReturn, Object... data) {
        Object items = data;
        if (data.length == 1) {
            if (data[0].getClass().isArray()) {
                items = data[0];
            } else if (data[0] instanceof Collection) {
                items = data[0];
            }
        }
        aGridDialog aGridDialog = new aGridDialog(header, items);
        aGridDialog.IsReturnValue = isReturn;
        return aGridDialog;
    }


    public static class Header {
        Map headers = new Hashtable();
        List<Map> fields = new LinkedList<>();

        public Header(int pageSize) {
            headers.put("type", "grid");
            headers.put("pageSize", pageSize);
        }

        public Header(String valueField, int pageSize) {
            headers.put("type", "dialog");
            headers.put("pageSize", pageSize);

            if (Utility.isEmpty(valueField)) {
                headers.put("type", "grid");
            } else {
                headers.put("ValueField", valueField);
            }
        }

        public Header put(String field, String name) {

            this.add(field, name);
            return this;
        }

        public void add(String field, String name) {
            Map map = Utility.find(fields, f -> field.equals(f.get("Name")));
            if (map == null) {
                map = new HashMap();
                map.put("Name", field);
                map.put("type", "string");

                fields.add(map);
            }
            Map map1 = new HashMap();
            map1.put("text", name);
            map.put("config", map1);
        }

        public Map getHeaderMap() {

            headers.put("fields", fields);
            return headers;
        }


    }

    protected UIGridDialog() {
        this.IsReturnValue = true;
    }

    public boolean IsReturnValue;

    protected abstract Map getHeader();

    protected abstract Map getData(Map paramsKey);


    private boolean AutoSearch;

    @Override
    protected String type() {

        return "Grid";
    }

    private boolean IsSearch;

    private String Keyword;

    protected void search(String model, String cmd, WebMeta param, String submodel, String subcmd) {
        this.search(model, cmd, param, submodel, subcmd, null);
    }

    protected void search(String model, String cmd, WebMeta param, String submodel, String subcmd, WebMeta send) {
        WebMeta p = new WebMeta();
        if (param != null) {
            p.put("params", param);
        }
        p.put("model", model).put("cmd", cmd);

        WebMeta sub = new WebMeta();

        sub.put("model", submodel).put("cmd", subcmd);
        if (send != null) {
            sub.put("send", send);
        }
        p.put("submit", sub);
        this.config.put("search", p);

    }

    public void menu(String text, String model, String cmd, String value) {
        this.menu(createMenu(text, model, cmd, value));
    }

    public void menu(WebMeta... menus) {
        this.config.put("menu", menus);
    }

    public void menu(String text, String model, String cmd, WebMeta param) {
        this.menu(createMenu(text, model, cmd, param));
    }

    private boolean IsPage;

    private WebMeta ValueField;
    private boolean IsAsyncData;

    public UIGridDialog setAsyncData(boolean asyncData) {
        IsAsyncData = asyncData;
        return this;
    }

    public UIGridDialog setReturnValue(boolean returnValue) {
        IsReturnValue = returnValue;
        return this;
    }


    public UIGridDialog setSearch(boolean search, boolean autoSearch) {
        IsSearch = search;
        AutoSearch = autoSearch;
        return this;
    }

    public UIGridDialog placeholder(String keyword) {
        Keyword = keyword;
        return this;
    }

    public UIGridDialog setPage(boolean page) {
        IsPage = page;
        return this;
    }

    public void valueField(String... value) {
        this.ValueField = new WebMeta(value);

    }


    @Override
    protected void initialization(WebContext context) {


        WebRequest request = context.request();
        WebResponse response = context.response();
        if (request.items().containsKey(this.asyncId())) {
            WebMeta meta = request.sendValues();
            if (meta != null) {
                response.redirect(this.getData(meta.map()));
            } else {
                Map paramKey = request.headers().meta(Dialog).map();// as Hashtable ??new Hashtable();
                response.redirect(this.getData(paramKey));
            }

        } else {
            Map p = getHeader();
            if (this.ValueField != null) {
                p.put("ValueField", this.ValueField);
            }
            if (IsPage) {
                WebMeta meta = request.sendValues();
                if (meta != null && meta.size() > 0) {
                    response.redirect(this.getData(meta.map()));
                } else if (request.arguments().size() == 0) {
                    search(request.model(), request.cmd(), null, request.model(), request.cmd(), null);
                } else {
                    WebMeta pa = new WebMeta(request.arguments());
                    search(request.model(), request.cmd(), pa, request.model(), request.cmd(), pa);
                    if (this.ValueField == null) {
                        p.put("send", this.asyncId());
                    }

                }
            }
            request.items().put(this.asyncId(), "Header");


            if (this.IsReturnValue) {
                p.put("type", "dialog");
            } else {
                p.put("type", "grid");
            }
            if (IsAsyncData) {
                this.config.put("Data", this.getData(new HashMap()));
            } else if (this.IsSearch) {
                p.put("search", Utility.isEmpty(this.Keyword) ? "搜索" : this.Keyword);
            }
            if (this.AutoSearch) {
                p.put("auto", true);
            }
            this.config.put("Header", p);
        }
        super.initialization(context);
    }
}